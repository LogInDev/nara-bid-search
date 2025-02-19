package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.DetailProduct;
import com.nivuskorea.procurement.entity.RestrictedRegion;
import com.nivuskorea.procurement.factory.WebDriverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaraService {

    private final WebDriverFactory webDriverFactory;
    private final DetailProductsService detailProductsService;
    private final RestrictedRegionService restrictedRegionService;
    private final BidInformationService bidInformationService;

    @Async
    public void BidAnnouncementSearch(){
        WebDriver driver = webDriverFactory.createWebDriver();
        List<BidInformationDto> bidList = new ArrayList<>();

        try {
            log.info("Selenium1 시작");
            driver.get("https://www.g2b.go.kr/");
            Thread.sleep(2000);

            // title이 "오늘 하루"로 시작하는 체크박스 찾기
            List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@type='checkbox' and starts-with(@title, '오늘 하루')]"));

            // 체크박스가 존재하면 클릭
            if (!checkboxes.isEmpty()) {
                checkboxes.forEach(WebElement::click);
                System.out.println("체크박스 클릭 완료"+checkboxes);
            } else {
                System.out.println("체크박스를 찾을 수 없음");
            }

            // [메인] - 1200 x 800
            // 메뉴 리스트 목록 선택
            final String menuListTagId = "mf_wfm_gnb_wfm_gnbMenu_btnSitemap";
            clickIcon(driver, menuListTagId);
            Thread.sleep(1000);

            // '입찰공고목록' 선택
            final String bidListTagId = "mf_wfm_gnb_wfm_gnbMenu_genMenu1_1_genMenu2_0_genMenu3_0_btnMenu3";
            clickIcon(driver, bidListTagId);
            Thread.sleep(1000);

            // [입찰공고목록]
            // '상세조건' 버튼 선택
            WebElement searchButton = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_btnS0004"));
            WebElement detailButton = searchButton.findElement(By.xpath("./following-sibling::div//input[@type='button' and @value='상세조건']"));
            detailButton.click();
            Thread.sleep(1000);

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
            List<RestrictedRegion> restrictedRegions = restrictedRegionService.selectByBidType(BidType.BID_ANNOUNCEMENT);
            final String selectOption = restrictedRegions.get(0).getRestrictedRegion();
            setSelectOption(driver, limitRegionTagId, selectOption);

            // '세부품명번호' 특정 - 4111249801(프로세스제어반)
            final String detailProductTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_dtlsPrnmNo";
            List<DetailProduct> detailProducts = detailProductsService.selectByBidType(BidType.BID_ANNOUNCEMENT);
            final String productNumber = detailProducts.get(0).getItemNumber();
            setSearchText(driver, detailProductTagId, productNumber);

            // 출력 총 index 100
            final String setIndexTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_sbxRecordCountPerPage1";
            final String setIndex = "100";
            setSelectOption(driver, setIndexTagId, setIndex);

            // 검색
            WebElement searchInput = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_bidPbancNm"));
            searchInput.sendKeys(Keys.ENTER);
            Thread.sleep(2000);

            // 검색 결과
            WebElement resultTable = driver.findElement(By.id("mf_wfm_container_tacBidPbancLst_contents_tab2_body_gridView1_body_table"));
            WebElement tbody = resultTable.findElement(By.tagName("tbody"));
            List<WebElement> rows = tbody.findElements(By.tagName("tr"));

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
                                .contractMethod(contractMethod)
                                .productId(detailProducts.get(0).getId())
                                .regionId(restrictedRegions.get(0).getId())
                                .build());
                    }
                });
            }

            bidInformationService.saveAllBids(bidList);

        } catch (Exception e) {
            log.error("Selenium1 실행 중 오류 발생: {}", e.getMessage());
        }finally{
//            driver.quit();
//            log.info("Selenium1 종료");
        }
    }


    /**
     * 클릭할 Tag Id를 특정해서 클릭
     * @param itemTagId 클릭할 Tag Id
     */
    private void clickIcon(WebDriver driver, String itemTagId) {
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
    private void setSearchText(WebDriver driver, String tagId, String searchText) {
        WebElement detailProductNumber = driver.findElement(By.id(tagId));
        detailProductNumber.sendKeys(searchText);
    }

    /**
     * select 태그에서 option 선택
     * @param tagId select Tag id
     * @param selectOption 선택할 selectOption
     */
    private void setSelectOption(WebDriver driver, String tagId, String selectOption) {
        WebElement selectElement = driver.findElement(By.id(tagId));
        Select select = new Select(selectElement);
        select.selectByVisibleText(selectOption);
    }

    /**
     * extractDates()의 분리된 결과를 담는 record
     * @param announcementDate 첫번째 LocalDateTime
     * @param deadline 두번째 LocalDateTime
     */
    private record StartToEndDates(LocalDateTime announcementDate, LocalDateTime deadline) {}




}
