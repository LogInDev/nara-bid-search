package com.nivuskorea.procurement.controller.selenium;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

        // [메인] - 1200 x 800
            // 메뉴 리스트 목록 선택
            WebElement listButton = driver.findElement(By.id("mf_wfm_gnb_wfm_gnbMenu_btnSitemap"));
            listButton.click();

            // '입찰공고목록' 선택
            WebElement selectOn = driver.findElement(By.id("mf_wfm_gnb_wfm_gnbMenu_genMenu1_1_genMenu2_0_genMenu3_0_btnMenu3"));
            selectOn.click();

        // [입찰공고목록]
            // '상세조건' 버튼 선택
            WebElement searchButton = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_btnS0004"));
            WebElement detailButton = searchButton.findElement(By.xpath("./following-sibling::div//input[@type='button' and @value='상세조건']"));
            detailButton.click();

            // '업무구분' - '물품'
            WebElement allCheckbox = driver.findElement(By.xpath(
                    "//*[@id='mf_wfm_container_tacBidPbancLst_contents_tab2_body_chkBidPbancSrchTyCd']//input[@type='checkbox' and @title='전체']"
            ));
            allCheckbox.click();
            Thread.sleep(2000);
            WebElement productCheckbox = driver.findElement(By.xpath(
                    "//*[@id='mf_wfm_container_tacBidPbancLst_contents_tab2_body_chkBidPbancSrchTyCd']//input[@type='checkbox' and @title='물품']"
            ));
            productCheckbox.click();
            Thread.sleep(1000);

            // '참가제한지역' 특정 - '전국(제한없음)' || 인천광역시
            final String limitRegionTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_sbxOdnLmtLgdngCd";
            final String selectOption = "전국(제한없음)";
            setSelectOption(limitRegionTagId, selectOption);

            // '세부품명번호' 특정 - 4111249801(프로세스제어반)
            final String detailProductTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_dtlsPrnmNo";
            final String productNumber = "4111249801";
            setSearchText(detailProductTagId, productNumber);

            // 출력 총 index 100
            final String setIndexTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_sbxRecordCountPerPage1";
            final String setIndex = "100";
            setSelectOption(setIndexTagId, setIndex);

            // 검색
            WebElement searchInput = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_bidPbancNm"));
            searchInput.sendKeys(Keys.ENTER);
            Thread.sleep(2000);

        // 검색 결과
            WebElement resultTable = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_gridView1_body_table"));
            WebElement tbody = resultTable.findElement(By.tagName("tbody"));
            List<WebElement> rows = tbody.findElements(By.tagName("tr"));

            if (!rows.isEmpty()) {
                for (WebElement row : rows) {

                }
                rows.stream().forEach(row -> {
                    String rowText = row.findElements(By.tagName("td")).get(5).getText();

                    if (rowText.length() > 0) {

                        System.out.println("rowText = " + rowText);
                    }
                });
            }

            return "Selenium 실행 완료";

        } catch (Exception e) {
            return "Selenium 실행 중 오류 발생: " + e.getMessage();
        }
    }

    /**
     * 검색어 검색
     * @param tagId 검색어 작성 Tag id
     * @param searchText 검색할 검색어
     */
    private void setSearchText(String tagId, String searchText) {
        WebElement detailProductNumber = driver.findElement(By.id(tagId));
        detailProductNumber.sendKeys(searchText);
    }

    /**
     * select 태그에서 option 선택
     * @param tagId select Tag id
     * @param selectOption 선택할 selectOption
     */
    private void setSelectOption(String tagId, String selectOption) {
        WebElement selectElement = driver.findElement(By.id(tagId));
        Select select = new Select(selectElement);
        select.selectByVisibleText(selectOption);
    }

    @GetMapping("/stop")
    public String stopSelenium() {
        driver.quit();
        return "Selenium 종료 완료";
    }
}
