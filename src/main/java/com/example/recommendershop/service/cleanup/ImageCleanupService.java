package com.example.recommendershop.service.cleanup;

import com.example.recommendershop.repository.*;
import com.example.recommendershop.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageCleanupService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FeaturedPostRepository featuredPostRepository;
    private final UserRepository userRepository;

    private final FileService fileService;

    public void cleanup() {
        log.info("=== START CLEANUP IMAGES ===");

        Set<String> usedImages = collectUsedImages();

        cleanFolder("products", usedImages);
        cleanFolder("categories", usedImages);
        cleanFolder("banners", usedImages);
        cleanFolder("users", usedImages);

        log.info("=== CLEANUP DONE ===");
    }

    private Set<String> collectUsedImages() {
        Set<String> used = new HashSet<>();

        // PRODUCT
        productRepository.findAll().forEach(p -> {
            if (p.getMainImage() != null) used.add(p.getMainImage());
            if (p.getImage() != null) used.addAll(p.getImage());
        });

        // CATEGORY
        categoryRepository.findAll().forEach(c -> {
            if (c.getImage() != null) used.add(c.getImage());
        });

        // BANNER
        featuredPostRepository.findAll().forEach(f -> {
            if (f.getUrl() != null) used.add(f.getUrl());
        });

        // USER
        userRepository.findAll().forEach(u -> {
            if (u.getAvatar() != null) used.add(u.getAvatar());
        });

        return used;
    }

    private void cleanFolder(String folder, Set<String> usedImages) {
        List<String> cloudImages = fileService.getAllImages(folder);

        for (String img : cloudImages) {
            if (!usedImages.contains(img)) {
                fileService.delete(img);
                log.info("Deleted orphan image: {}", img);
            }
        }
    }
}