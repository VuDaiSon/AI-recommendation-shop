package com.example.recommendershop.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ❌ KHÔNG DÙNG backendUrl nữa
    public String post(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            // ✅ CHUẨN PRODUCTION: chỉ trả PATH
            return "/uploads/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Upload lỗi: " + e.getMessage());
        }
    }

    public void deleteFileByUrl(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) return;

            // lấy tên file từ URL hoặc PATH
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            Path path = Paths.get(uploadDir, fileName);

            Files.deleteIfExists(path);

        } catch (Exception e) {
            System.out.println("Không xóa được file: " + e.getMessage());
        }
    }
}