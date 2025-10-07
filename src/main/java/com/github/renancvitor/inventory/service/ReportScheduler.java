package com.github.renancvitor.inventory.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportScheduler {

    private final MovementReportService movementReportService;

    @Scheduled(cron = "0 0 17 * * *", zone = "America/Sao_Paulo")
    public void sendDailyMovementReport() {
        movementReportService.sendDailyReport();
    }

}
