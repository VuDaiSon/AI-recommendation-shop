package com.example.recommendershop.controller;

import com.example.recommendershop.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 🔥 DELETE IMAGE BY URL
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String url = body.get("url");

        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body("Missing url");
        }

        fileService.delete(url);

        return ResponseEntity.ok("Deleted");
    }
}