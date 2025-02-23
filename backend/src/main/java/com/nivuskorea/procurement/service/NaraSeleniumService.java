package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.*;
import com.nivuskorea.procurement.factory.WebDriverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaraSeleniumService {

    private final WebDriverFactory webDriverFactory;
    private final DetailProductsService detailProductsService;
    private final RestrictedRegionService restrictedRegionService;
    private final BidInformationService bidInformationService;
    private final ContractTypesService contractTypesService;

    @Async
    public CompletableFuture<WebDriver> BidAnnouncementSearch(){
        WebDriver driver = webDriverFactory.createWebDriver();
        List<BidInformationDto> bidList = new ArrayList<>();

        try {
            log.info("Selenium1 시작");
            setUpMeueList(driver);
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

            // 출력 총 index 100
            final String setIndexTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_sbxRecordCountPerPage1";
            final String setIndex = "100";
            setSelectOption(driver, setIndexTagId, setIndex);

            // '참가제한지역' 특정 - '전국(제한없음)' || '인천광역시'
            final String limitRegionTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_sbxOdnLmtLgdngCd";
            List<RestrictedRegion> restrictedRegions = restrictedRegionService.selectByBidType(BidType.BID_ANNOUNCEMENT);

            // '계약방법' 특정 - '일반경쟁' || '제한경쟁'
            final String contractMethodTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_sbxBidCtrtMthdCd";
            List<ContractType> contractTypes = contractTypesService.selectByBidType(BidType.BID_ANNOUNCEMENT);


            // '세부품명번호' 특정 - 4111249801(프로세스제어반), 계장제어장치, 유량계
            final String detailProductTagId = "mf_wfm_container_tacBidPbancLst_contents_tab2_body_dtlsPrnmNo";
            List<DetailProduct> detailProducts = detailProductsService.selectByBidType(BidType.BID_ANNOUNCEMENT);

            restrictedRegions.forEach(restrictedRegion->{
                final String selectOption = restrictedRegion.getRestrictedRegion();
                setSelectOption(driver, limitRegionTagId, selectOption);

                contractTypes.forEach(contractType -> {
                    final String contractOption = contractType.getContract().getDescription();
                    setSelectOption(driver, contractMethodTagId, contractOption);

                    detailProducts.forEach(detailProduct -> {
                        final String productNumber = detailProduct.getItemNumber();
                        setSearchText(driver, detailProductTagId, productNumber);

                        try {
                            Thread.sleep(1000);
                            searchResult(driver, bidList, detailProduct, restrictedRegion, contractType);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });

                });
            });
            bidInformationService.saveBidAnnAllBids(bidList);

        } catch (Exception e) {
            log.error("Selenium1 실행 중 오류 발생: {}", e.getMessage());
        }

        return CompletableFuture.completedFuture(driver);
    }


    @Async
    public CompletableFuture<WebDriver> PreStandardSearch(){
        log.info("새로운 Web");
        WebDriver driver = webDriverFactory.createWebDriver();
        List<BidInformationDto> bidList = new ArrayList<>();

        try {
            log.info("Selenium2 시작");
            setUpMeueList(driver);
            Thread.sleep(1000);

            // '발주목록' 선택
            final String procurementListTagId = "mf_wfm_gnb_wfm_gnbMenu_genMenu1_0_genMenu2_0_btnMenu2_span";
            clickIcon(driver, procurementListTagId);
            Thread.sleep(3000);

            // [발주목록]
            // '사전규격공개'
            final String beforeOpenTagId = "mf_wfm_container_radSrchTy_input_1";
            WebElement beforeOpen = driver.findElement(By.id(beforeOpenTagId));
            clickIcon(driver, beforeOpenTagId);


            // '상세조건' 버튼 선택
            WebElement searchButton = driver.findElement(By.id("mf_wfm_container_btnIntz"));
            WebElement detailButton = searchButton.findElement(By.xpath("./following-sibling::div//input[@type='button' and @value='상세조건']"));
            detailButton.click();
            Thread.sleep(2000);

            // '업무구분' - '물품'
            final String productCheckboxTagId = "mf_wfm_container_chkRqdcBsneSeCd_input_1";
            clickIcon(driver, productCheckboxTagId);

            // 출력 총 index 100
            final String setIndexTagId = "mf_wfm_container_sbxRecordCountPerPage";
            final String setIndex = "100";
            setSelectOption(driver, setIndexTagId, setIndex);

            // '세부품명번호' 특정 - 4111249801(프로세스제어반), 계장제어장치, 유량계
            WebElement inputElement = driver.findElement(By.xpath("//tr[@id='mf_wfm_container_grpItem02']//input[@title='세부품명번호']"));
            List<DetailProduct> detailProducts = detailProductsService.selectByBidType(BidType.BID_ANNOUNCEMENT);
            inputElement.sendKeys("4111249801");
            Thread.sleep(1000);

            // '검색' 버튼 클릭
            final String searchBtnTagId = "mf_wfm_container_btnS0001";
            WebElement searchBtn = driver.findElement(By.id(searchBtnTagId));
            searchBtn.sendKeys(Keys.ENTER);
            Thread.sleep(2000);

            // 검색 결과
            WebElement resultTable = driver.findElement(By.id("mf_wfm_container_gridView1_body_table"));
            WebElement tbody = resultTable.findElement(By.tagName("tbody"));
            List<WebElement> rows = tbody.findElements(By.tagName("tr"));

            if (!rows.isEmpty()) {
                rows.forEach(row -> {
                    System.out.println("row = " + row);
                    final WebElement td = row.findElements(By.tagName("td")).get(4);
                    System.out.println("td = " + td);
                });
            }


//
//            restrictedRegions.forEach(restrictedRegion->{
//                final String selectOption = restrictedRegion.getRestrictedRegion();
//                setSelectOption(driver, limitRegionTagId, selectOption);
//
//                contractTypes.forEach(contractType -> {
//                    final String contractOption = contractType.getContract().getDescription();
//                    setSelectOption(driver, contractMethodTagId, contractOption);
//
//                    detailProducts.forEach(detailProduct -> {
//                        final String productNumber = detailProduct.getItemNumber();
//                        setSearchText(driver, detailProductTagId, productNumber);
//
//                        try {
//                            Thread.sleep(1000);
//                            searchResult(driver, bidList, detailProduct, restrictedRegion, contractType);
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//
//                });
//            });
//            bidInformationService.saveAllBids(bidList);

        } catch (Exception e) {
            log.error("Selenium2 실행 중 오류 발생: {}", e.getMessage());
        }

        return CompletableFuture.completedFuture(driver);
    }


    /**
     * '검색' 클릭 후 검색 결과 반환
     * @param driver 현재 크롬
     * @param bidList 검색 결과 저장 리스트
     * @param detailProduct 검색한 세부품목 객체
     * @param restrictedRegion 검색한 제한지역 객체
     * @throws InterruptedException 크롬 예외 처리
     */
    private void searchResult(WebDriver driver, List<BidInformationDto> bidList, DetailProduct detailProduct, RestrictedRegion restrictedRegion, ContractType contractType) throws InterruptedException {
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
                String cancelText = row.findElements(By.tagName("td")).get(4).getText().trim();
                if (cancelText.contains("취소")) return;
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
                            .productId(detailProduct.getId())
                            .regionId(restrictedRegion.getId())
                            .contractId(contractType.getId())
                            .build());
                }
            });
        }
    }

    /**
     * 나라장터 메인에서 메뉴 리스트
     * @param driver 크롬
     * @throws InterruptedException 크롬 관련 예외 처리
     */
    private void setUpMeueList(WebDriver driver) throws InterruptedException {
        final String url = "https://www.g2b.go.kr/";
        driver.get(url);
        // WebDriverWait을 사용하여 특정 요소가 로드될 때까지 기다리기
        final int limitSecond = 60;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(limitSecond));

        final String menuListTagId = "mf_wfm_gnb_wfm_gnbMenu_btnSitemap";
        int retryCount = 0;
        final int masRetries = 5;

        boolean isLoaded = false;

        do {
            try {
                log.info("페이지 로드 시도: {} ({}번째 시도)", url, retryCount + 1);
                driver.get(url);

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(menuListTagId)));
                isLoaded = true;
            } catch (TimeoutException e) {
                retryCount++;
                log.warn("요소가 {}초 동안 로드되지 않음. 재시도 중... ({} / {})", limitSecond, retryCount, masRetries);

                if (retryCount >= masRetries) {
                    log.error("최대 재시도 횟수 초과. 페이지 로드 실패: {}", url);
                    throw e;
                }
            }
        } while (!isLoaded);

        log.info("페이지 로드 완료");
        Thread.sleep(5000);

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
        clickIcon(driver, menuListTagId);
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
        detailProductNumber.clear();
        detailProductNumber.sendKeys(searchText);
    }

    /**
     * select 태그에서 option 선택
     *
     * @param tagId select 태그의 ID
     * @param option 선택할 옵션
     */
    private void setSelectOption(WebDriver driver, String tagId, Object option) {
        WebElement selectElement = driver.findElement(By.id(tagId));
        Select select = new Select(selectElement);
        select.selectByVisibleText(option.toString());
    }


    /**
     * extractDates()의 분리된 결과를 담는 record
     * @param announcementDate 첫번째 LocalDateTime
     * @param deadline 두번째 LocalDateTime
     */
    private record StartToEndDates(LocalDateTime announcementDate, LocalDateTime deadline) {}




}
