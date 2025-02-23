package com.nivuskorea.procurement.controller.selenium;

import com.nivuskorea.procurement.service.BidInformationService;
import com.nivuskorea.procurement.service.NaraSeleniumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/selenium")
@RequiredArgsConstructor
public class SeleniumController {

    private final BidInformationService bidInformationService;
    private final NaraSeleniumService naraSeleniumService;

    @GetMapping("/run")
    public String runSelenium() {

//        final CompletableFuture<WebDriver> bidFuture = naraService.BidAnnouncementSearch();
//        final CompletableFuture<WebDriver> procurementFuture = naraService.PreStandardSearch();

        return "Selenium 실행 완료";

    }

    @GetMapping("/quit")
    private String driverClose(WebDriver webDriver) {
        if (webDriver != null) {
            webDriver.quit();
            log.info("WebDriver quit - {}", webDriver.getTitle());
        }
        return "Selenium 종료 완료";
    }

}
