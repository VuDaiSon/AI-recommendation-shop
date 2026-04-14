package com.example.recommendershop.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final Cloudinary cloudinary;

    private static final int MAX_RETRY = 3;

    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String publicId = extractPublicId(imageUrl);

        if (publicId == null || publicId.isBlank()) {
            log.warn("Invalid publicId from url: {}", imageUrl);
            return;
        }

        log.info("Deleting image with publicId: {}", publicId);
        deleteWithRetry(publicId, MAX_RETRY);
    }

    private void deleteWithRetry(String publicId, int retry) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result =
                    (Map<String, Object>) cloudinary.uploader()
                            .destroy(publicId, ObjectUtils.asMap(
                                    "resource_type", "image"
                            ));

            String status = (String) result.get("result");

            if ("ok".equals(status) || "not found".equals(status)) {
                log.info("Deleted image: {}", publicId);
                return;
            }

            if (retry > 0) {
                log.warn("Retry delete ({}) for image: {}", MAX_RETRY - retry + 1, publicId);
                deleteWithRetry(publicId, retry - 1);
            } else {
                log.error("Delete failed after retry: {}", publicId);
            }

        } catch (Exception e) {
            if (retry > 0) {
                log.warn("Exception -> retry delete ({}) for image: {}", MAX_RETRY - retry + 1, publicId);
                deleteWithRetry(publicId, retry - 1);
            } else {
                log.error("Delete failed after retry (exception): {}", publicId, e);
            }
        }
    }

    // ================================
    // 🔥 GET ALL IMAGES BY FOLDER
    // ================================
    public List<String> getAllImages(String folder) {
        List<String> urls = new ArrayList<>();

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.api().resources(
                    ObjectUtils.asMap(
                            "type", "upload",
                            "prefix", folder,
                            "max_results", 500
                    )
            );

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resources =
                    (List<Map<String, Object>>) result.get("resources");

            if (resources != null) {
                for (Map<String, Object> r : resources) {
                    String url = (String) r.get("secure_url");
                    if (url != null) {
                        urls.add(url);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error fetching images from Cloudinary folder: {}", folder, e);
        }

        return urls;
    }

    // ================================
    // 🔥 FIXED: EXTRACT PUBLIC ID
    // ================================
    private String extractPublicId(String url) {
        try {
            // tách phần sau /upload/
            String[] parts = url.split("/upload/");
            if (parts.length < 2) return null;

            String path = parts[1];

            // remove version (v123456/)
            path = path.replaceFirst("^v\\d+/", "");

            // remove extension (.jpg, .png...)
            int dotIndex = path.lastIndexOf(".");
            if (dotIndex != -1) {
                path = path.substring(0, dotIndex);
            }

            return path;

        } catch (Exception e) {
            log.error("Error extracting publicId from url: {}", url, e);
            return null;
        }
    }
}