package com.example.recommendershop.service.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
    public String post(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            return "http://localhost:8080/uploads/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Upload lỗi");
        }
    }
    public void deleteFile(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) return;

            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path path = Paths.get("uploads/" + fileName);

            Files.deleteIfExists(path);

        } catch (Exception e) {
            System.out.println("Không thể xóa file: " + e.getMessage());
        }
    }
    public void deleteFileByUrl(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.contains("/uploads/")) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/uploads/") + 9);
                Path filePath = Paths.get("uploads", fileName);
                Files.deleteIfExists(filePath);
            }
        } catch (Exception e) {
            System.out.println("Không xóa được file ảnh: " + e.getMessage());
        }
    }
}