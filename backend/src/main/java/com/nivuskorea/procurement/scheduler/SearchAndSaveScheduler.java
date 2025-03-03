package com.nivuskorea.procurement.scheduler;

import com.nivuskorea.procurement.service.NaraBidAnnApiService;
import com.nivuskorea.procurement.service.NaraProcurementApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchAndSaveScheduler {

    private final NaraBidAnnApiService naraBidAnnApiService;
    private final NaraProcurementApiService naraProcurementApiService;

//    @Scheduled(cron = "0 0 3 * * ?")
    public void searchAndSave() {
        try {
            log.info("Scheduled task started at : {}", LocalDateTime.now());

            // 입찰 공고 검색 및 저장
            naraBidAnnApiService.bidAnnApi();
            // 발주 > 사전 규격 검색 및 저장
            naraProcurementApiService.keywordApi();
            naraProcurementApiService.procurementApi();

            log.info("Scheduled task completed at : {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error during scheduled task execution", e);
        }
    }
}
