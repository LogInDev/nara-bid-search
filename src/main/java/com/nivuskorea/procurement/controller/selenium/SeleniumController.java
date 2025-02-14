package com.nivuskorea.procurement.controller.selenium;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/selenium")
@RequiredArgsConstructor
public class SeleniumController {

    private final WebDriver driver;

    @GetMapping("/run")
    public String runSelenium() {
        try {
            driver.get("https://www.g2b.go.kr/");
            Thread.sleep(2000);

            WebElement listButton = driver.findElement(By.id("mf_wfm_gnb_wfm_gnbBtm_btnMenuOpen"));
            listButton.click();

            WebElement selectList = driver.findElement(By.id("mf_wfm_gnb_wfm_gnbMenu_genDepth1_1_btn_menuLvl1"));
            Actions actions = new Actions(driver);
            actions.moveToElement(selectList).perform();
            Thread.sleep(1000);

            WebElement aTag = driver.findElement(By.id("mf_wfm_gnb_wfm_gnbMenu_genDepth1_1_genDepth2_0_btn_menuLvl2"));
            WebElement selectOn = aTag.findElement(By.xpath("./following-sibling::input[@type='button']"));
            selectOn.click();

            WebElement selectItem = driver.findElement(By.id("mf_wfm_gnb_wfm_gnbMenu_genDepth1_1_genDepth2_0_genDepth3_0_btn_menuLvl3"));
            selectItem.click();

            WebElement searchButton = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_btnS0004"));
            WebElement detailButton = searchButton.findElement(By.xpath("./following-sibling::div//input[@type='button' and @value='상세조건']"));
            detailButton.click();

            //
            WebElement searchInput = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_bidPbancNm"));
            searchInput.sendKeys("지방상수도");
            Thread.sleep(2000);
            searchInput.sendKeys(Keys.ENTER);

            return "Selenium 실행 완료";

        } catch (Exception e) {
            return "Selenium 실행 중 오류 발생: " + e.getMessage();
        }
    }

    @GetMapping("/stop")
    public String stopSelenium() {
        driver.quit();
        return "Selenium 종료 완료";
    }
}
