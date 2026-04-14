package com.example.recommendershop.service.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


import java.util.Map;

@Service
public class FileService {

    private final Cloudinary cloudinary;

    public FileService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String post(MultipartFile file) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "products")
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Upload lỗi: " + e.getMessage());
        }
    }

    public void delete(String imageUrl) {
        try {
            if (imageUrl == null) return;

            // lấy public_id từ URL
            String[] parts = imageUrl.split("/");
            String fileName = parts[parts.length - 1];
            String publicId = "products/" + fileName.substring(0, fileName.lastIndexOf("."));

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {
            System.out.println("Không xóa được ảnh: " + e.getMessage());
        }
    }
}