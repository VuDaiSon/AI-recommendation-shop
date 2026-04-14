package com.example.recommendershop.scheduler;

import com.example.recommendershop.service.cleanup.ImageCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupScheduler {

    private final ImageCleanupService cleanupService;

    @Scheduled(cron = "0 0 3 * * ?") // 3h sáng mỗi ngày
    public void run() {
        log.info("Running scheduled image cleanup...");
        cleanupService.cleanup();
    }
}