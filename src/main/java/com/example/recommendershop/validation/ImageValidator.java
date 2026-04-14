package com.example.recommendershop.validation;

import com.example.recommendershop.exception.MasterException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ImageValidator {

    private static final String CLOUDINARY_PREFIX = "https://res.cloudinary.com/";

    private static final Pattern IMAGE_PATTERN =
            Pattern.compile(".*\\.(jpg|jpeg|png|webp)$", Pattern.CASE_INSENSITIVE);

    private static final int MAX_IMAGES = 10;

    public void validateMainImage(String url) {
        if (url == null || url.isBlank()) {
            throw new RuntimeException("Ảnh chính không được để trống");
        }

        validateSingleImage(url);
    }

    public void validateImages(List<String> images) {
        if (images == null) return;

        if (images.size() > MAX_IMAGES) {
            throw new RuntimeException("Tối đa 10 ảnh");
        }

        // 🔥 CHECK DUPLICATE
        if (images.size() != new HashSet<>(images).size()) {
            throw new RuntimeException("Danh sách ảnh bị trùng");
        }

        for (String url : images) {
            validateSingleImage(url);
        }
    }
    private void validateSingleImage(String url) {
        if (!url.startsWith(CLOUDINARY_PREFIX)) {
            throw new RuntimeException("Ảnh không hợp lệ");
        }

        if (!IMAGE_PATTERN.matcher(url).matches()) {
            throw new MasterException(HttpStatus.BAD_REQUEST, "Ảnh sai định dạng");        }
    }
    public void validateFolder(String url, String expectedFolder) {
        if (url == null) return;

        if (!url.contains("/upload/")) {
            throw new RuntimeException("URL ảnh không hợp lệ");
        }

        String[] parts = url.split("/upload/");
        if (parts.length < 2) {
            throw new MasterException(HttpStatus.BAD_REQUEST, "URL ảnh không hợp lệ");
        }

        String path = parts[1];
        // remove version v123/
        if (path.startsWith("v")) {
            path = path.substring(path.indexOf("/") + 1);
        }

        if (!path.startsWith(expectedFolder + "/")) {
            throw new RuntimeException("Ảnh không thuộc folder hợp lệ: " + expectedFolder);
        }
    }
}