package com.nivuskorea.procurement.controller.selenium;

import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.BidInformation;
import com.nivuskorea.procurement.entity.Category;
import com.nivuskorea.procurement.service.BidInformationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/selenium")
@RequiredArgsConstructor
public class SeleniumController {

    private final WebDriver driver;
    private final BidInformationService bidInformationService;

    @GetMapping("/run")
    public String runSelenium() {
        try {
            driver.get("https://www.g2b.go.kr/");
            Thread.sleep(2000);

        // [메인] - 1200 x 800
            // 메뉴 리스트 목록 선택
            final String menuListTagId = "mf_wfm_gnb_wfm_gnbMenu_btnSitemap";
            clickIcon(menuListTagId);

            // '입찰공고목록' 선택
            final String bidListTagId = "mf_wfm_gnb_wfm_gnbMenu_genMenu1_1_genMenu2_0_genMenu3_0_btnMenu3";
            clickIcon(bidListTagId);

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

            List<BidInformationDto> bidList = new ArrayList<>();

            if (!rows.isEmpty()) {
                rows.forEach(row -> {
                    String rowText = row.findElements(By.tagName("td")).get(5).getText();
                    if (!rowText.isEmpty()) {
                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        String result = rowText;
                        if (rowText.contains("-")) {
                            result = rowText.split("-")[0];
                        }

                        String dateAll = row.findElements(By.tagName("td")).get(9).getText();
                        final StartToEndDates dates = extractDates(dateAll);

                        //  추가 요약 정보
                        WebElement targetCell = row.findElements(By.tagName("td")).get(15); // 15번째 열 선택
                        WebElement button = targetCell.findElement(By.tagName("button"));   // 버튼 요소 찾기
                        WebElement div = button.findElement(By.tagName("div"));             // div 요소 찾기
                        List<WebElement> paragraphs = div.findElements(By.tagName("p"));    // 모든 <p> 태그 가져오기

                        String contractMethod = ""; // 낙찰방법
                        String estimatedBudget = ""; // 배정예산

                        for (WebElement p : paragraphs) {
                            // JavaScriptExecutor로 텍스트 가져오기 (렌더링된 데이터)
                            String text = (String) js.executeScript("return arguments[0].textContent;", p);
                            if (text.startsWith("낙찰방법 :")) {
                                contractMethod = text.replace("낙찰방법 :", "").trim();
                            } else if (text.startsWith("배정예산 :")) {
                                estimatedBudget = text.replace("배정예산 :", "").trim()
                                        .replaceAll("[^0-9]", "");
                            }
                        }
                        bidList.add(BidInformationDto.builder()
                                .category(row.findElements(By.tagName("td")).get(1).getText())
                                .bidType("입찰공고")
                                .title(row.findElements(By.tagName("td")).get(6).getText())
                                .institution(row.findElements(By.tagName("td")).get(8).getText())
                                .bidNumber(result)
                                .estimatedAmount(Long.parseLong(estimatedBudget))
                                .announcementDate(dates.announcementDate())
                                .deadline(dates.deadline())
                                .contractMethod(contractMethod).build());
                    }
                });
                bidInformationService.saveAllBids(bidList);
            }

            return "Selenium 실행 완료";

        } catch (Exception e) {
            return "Selenium 실행 중 오류 발생: " + e.getMessage();
        }
    }

    /**
     * 클릭할 Tag Id를 특정해서 클릭
     * @param itemTagId 클릭할 Tag Id
     */
    private void clickIcon(String itemTagId) {
        WebElement selectOn = driver.findElement(By.id(itemTagId));
        selectOn.click();
    }

    /**
     * 일시를 나타내는 2개의 데이터가 ()로 구분되어 같이 있는 경우 분리하는 기능
     * @param dateAll LocalDateTime1(LocalDateTmie2
     * @return LocalDateTime1, LocalDateTime2
     */
    private StartToEndDates extractDates(String dateAll) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String date1 = dateAll.split("\\(")[0].trim();
        String date2 = dateAll.split("\\(")[1].split("\\)")[0].trim();
        LocalDateTime announcementDate = LocalDateTime.parse(date1, formatter);
        LocalDateTime deadline = LocalDateTime.parse(date2, formatter);

        return new StartToEndDates(announcementDate, deadline);
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

    /**
     * extractDates()의 분리된 결과를 담는 record
     * @param announcementDate 첫번째 LocalDateTime
     * @param deadline 두번째 LocalDateTime
     */
    private record StartToEndDates(LocalDateTime announcementDate, LocalDateTime deadline) {}
}
