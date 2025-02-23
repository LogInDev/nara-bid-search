package com.nivuskorea.procurement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaraApiService {

    private final DetailProductsService detailProductsService;
    private final BidInformationService bidInformationService;
    private final ProjectSearchKeywordsService projectSearchKeywordsService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    HttpClient client = HttpClient.newHttpClient();

    /**
     * 발주목록 > 사전규격 > 물품 > 세부품목별 공고 저장 process
     */
    public void procurementApi() {

        try {
            final String sessionId = getSessionId();

            List<DetailProduct> detailProducts = detailProductsService.selectByBidType(BidType.PRE_STANDARD);

            for (DetailProduct detailProduct : detailProducts) {
                List<String> orderPlanNoList = getOrderPlanNoList(sessionId, detailProduct.getItemNumber(), "", "01");
                processOrderPlans(sessionId, orderPlanNoList, detailProduct);
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 검색어 결과 조회
     */
    public void keywordApi(){
        try {
            final List<ProjectSearchKeyword> projectSearchKeywords = projectSearchKeywordsService.selectByBidType(BidType.PRE_STANDARD);
            final String sessionId = getSessionId();

            for (ProjectSearchKeyword projectSearchKeyword : projectSearchKeywords) {
                List<String> orderPlanNoList = getOrderPlanNoList(sessionId, "", projectSearchKeyword.getSearchKeyword(), "03 05");
                processOrderPlans(sessionId, orderPlanNoList, projectSearchKeyword);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 공고 번호별 검색 요청 및 응답 결과 Entity 리스트로 생성
     * @param sessionId 요청 세션 id
     * @param orderPlanNoList 공고 번호 리스트
     * @param detailProduct 세부품목객체
     */
    private void processOrderPlans(String sessionId, List<String> orderPlanNoList, DetailProduct detailProduct) {
        List<BidInformationDto> bidList = new ArrayList<>();
        for (String orderPlanNo : orderPlanNoList) {
            try {
                HttpResponse<String> response = getDetailResultHttpResponse(sessionId, orderPlanNo);
                parseResponse(response.body(), bidList, detailProduct);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        bidInformationService.savePreStdAllBids(bidList);

    }
    private void processOrderPlans(String sessionId, List<String> orderPlanNoList, ProjectSearchKeyword projectSearchKeyword) {
        List<BidInformationDto> bidList = new ArrayList<>();
        for (String orderPlanNo : orderPlanNoList) {
            try {
                HttpResponse<String> response = getDetailResultHttpResponse(sessionId, orderPlanNo);
                parseResponse(response.body(), bidList, projectSearchKeyword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        bidInformationService.savePreStdKeywordsAllBids(bidList);

    }

    /**
     * 공고 상세 정보인 응답 객체를 Entity에 맞게 변환하여 Entity 리스트에 추가
     * @param responseBody 공고 상세 정보 응답 객체
     * @param bidList Entity 리스트
     * @param detailProduct 세부 품목 객체
     */
    private void parseResponse(String responseBody, List<BidInformationDto> bidList, DetailProduct detailProduct) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        if (rootNode.has("dlBfSpecM")) {
            JsonNode item = rootNode.get("dlBfSpecM");
            bidList.add(BidInformationDto.builder()
                    .category(item.get("prcmBsneSeCdNm").asText(""))
                    .bidType("사전규격")
                    .title(StringEscapeUtils.unescapeHtml4(item.get("bfSpecNm").asText("")))    // 공고명
                    .institution(StringEscapeUtils.unescapeHtml4(item.get("dmstUntyGrpNm").asText(""))) // 수요기관
                    .bidNumber(StringEscapeUtils.unescapeHtml4(item.get("bfSpecRegNo").asText(""))) // 공고번호
                    .estimatedAmount(item.get("alotBgtPrspAmt").asLong(0L)) // 기초금액(배정예산액)
                    .announcementDate(parseDate(item.path("specRlsDt"), formatter)) // 공고일(공개일시)
                    .deadline(parseDate(item.path("opnnRegDdlnDt"), formatter)) // 마감일(의견등록마감)
                    .contractMethod("")
                    .productId(detailProduct.getId())
                    .build());
        }
    }
    // 키워드 검색용
    private void parseResponse(String responseBody, List<BidInformationDto> bidList, ProjectSearchKeyword projectSearchKeyword) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        if (rootNode.has("dlBfSpecM")) {
            JsonNode item = rootNode.get("dlBfSpecM");
            bidList.add(BidInformationDto.builder()
                    .category(item.get("prcmBsneSeCdNm").asText(""))
                    .bidType("사전규격")
                    .title(StringEscapeUtils.unescapeHtml4(item.get("bfSpecNm").asText("")))    // 공고명
                    .institution(StringEscapeUtils.unescapeHtml4(item.get("dmstUntyGrpNm").asText(""))) // 수요기관
                    .bidNumber(StringEscapeUtils.unescapeHtml4(item.get("bfSpecRegNo").asText(""))) // 공고번호
                    .estimatedAmount(item.get("alotBgtPrspAmt").asLong(0L)) // 기초금액(배정예산액)
                    .announcementDate(parseDate(item.path("specRlsDt"), formatter)) // 공고일(공개일시)
                    .deadline(parseDate(item.path("opnnRegDdlnDt"), formatter)) // 마감일(의견등록마감)
                    .contractMethod("")
                    .keywordId(projectSearchKeyword.getId())
                    .build());
        }
    }

    /**
     * 발주목록 > 사전규격 공고 검색
     * @param sessionId 요청할 세션 id
     * @param itemNumber 세부 품목 번호('물품' 검색 시)
     * @param searchKeyword 검색할 검색어('일반용역', '기술용역' 검색 시)
     * @param prcmBsneSeCd '물품' : 01 || '일반용역' : 03 || '기술용역' : 05
     * @return 검색 결과 중 공고 번호 리스트로 반환
     */
    private List<String> getOrderPlanNoList(String sessionId, String itemNumber, String searchKeyword, String prcmBsneSeCd) throws IOException, InterruptedException {
        List<String> orderPlanNoList = new ArrayList<>();
        final HttpRequest dataRequest = buildOrderRequest(sessionId, itemNumber, searchKeyword, prcmBsneSeCd);
        // 요청 객체로 세부 품목별 사전규격 검색 결과 응답 객체 반환
        HttpResponse<String> dataResponse = client.send(dataRequest, HttpResponse.BodyHandlers.ofString());

        // 응답 객체 처리 - 검색 결과 중 공고 번호만 추출
        JsonNode rootNode = objectMapper.readTree(dataResponse.body());
        if (rootNode.has("dlOderReqL")) {
            for (JsonNode item : rootNode.get("dlOderReqL")) {
                orderPlanNoList.add(item.get("oderPlanNo").asText());
            }
        }
        log.info("orderPlanNoList : {}", orderPlanNoList);
        return orderPlanNoList;
    }

    /**
     * 세부품목별 사전규격 검색 결과 리스트 응답을 위한 요청객체 생성
     *
     * @param sessionId  요청에 보낼 세션 id
     * @param itemNumber 세부품목번호
     * @return 요청객체
     */
    private static HttpRequest buildOrderRequest(String sessionId, String itemNumber, String searchKeyword, String prcmBsneSeCd) {
        // 요청일자 setting
        LocalDate today = LocalDate.now();
        final LocalDate oneMonthAgo = today.minusMonths(1);
        final LocalDate lastYearDec = today.minusYears(1).withMonth(12);
        final LocalDate thisYearDec = today.withMonth(12);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyMMdd");
        final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

        // 요청 객체 생성
        Map<String, Object> dlOderReqSrchM = new HashMap<>();
        dlOderReqSrchM.put("srchTy", "0002");
        dlOderReqSrchM.put("bizNm", searchKeyword);             // 검색할 검색어
        dlOderReqSrchM.put("prgrsBgngYmd", oneMonthAgo.format(formatter)); // 오늘일자 기준 한달 전
        dlOderReqSrchM.put("prgrsEndYmd", today.format(formatter));  // 오늘 일자
        dlOderReqSrchM.put("currentPage", 1);           // 불러올 페이지
        dlOderReqSrchM.put("recordCountPerPage", 100);  // 불러올 결과 index 수
        dlOderReqSrchM.put("dtlsPrnmNo", itemNumber);   // 검색할 세부 품명 번호
        dlOderReqSrchM.put("prcmBsneSeCd", prcmBsneSeCd);
        dlOderReqSrchM.put("oderStTm", lastYearDec.format(yearMonthFormatter));   // 작년 12월
        dlOderReqSrchM.put("oderEdYm", thisYearDec.format(yearMonthFormatter));   // 올해 12월

//        log.info("dlOderReqSrchM : {}", dlOderReqSrchM);

        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("dlOderReqSrchM", dlOderReqSrchM);

        String jsonInputString = convertToJson(jsonRequest);

        String cookies = "JSESSIONID=" + sessionId
                + "; WHATAP=z28iebqcob0r3d"
                + "; XTVID=A2502222156174447"
                + "; Path=/"
                + "; infoSysCd=%EC%A0%95010029"
                + "; _harry_url=https%3A//www.g2b.go.kr/"
                + "; XTSID=A250222215618811067"
                + "; lastAccess=1740228992438";

        HttpRequest dataRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.g2b.go.kr/pr/prc/prca/OderReq/selectOderReqList.do"))
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Accept", "application/json")
                .header("Referer", "https://www.g2b.go.kr/")
                .header("Cookie", cookies) // 갱신된 쿠키 포함
                .header("menu-info", "{\"menuNo\":\"13713\",\"menuCangVal\":\"PRCA001_04\",\"bsneClsfCd\":\"%EC%97%85130025\",\"scrnNo\":\"00963\"}")
                .header("submissionid", "mf_wfm_container_smSearchOderReqLstList")
                .header("target-id", "btnS0001")
                .header("usr-id", "null")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();

        return dataRequest;
    }

    private static String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }


    /**
     * 요청에 보낼 공통 세션 id 반환
     * @return 세션 id
     */
    private String getSessionId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.g2b.go.kr/co/coz/coza/util/getSession.do"))
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Referer", "https://www.g2b.go.kr/")
                .header("User-Agent", "Mozilla/5.0")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return extractJSessionId(response.headers().map());
    }

    /**
     * 공고별 상세 정보 요청 후 결과 응답 객체 반환
     * @param sessionId 요청할 세션 id
     * @param orderPlanNo 검색할 공고 번호
     * @return 공고 번호에 따른 상세 정보 응답 객체로 반환
     */
    private HttpResponse<String> getDetailResultHttpResponse(String sessionId, String orderPlanNo) throws IOException, InterruptedException {
        String jsonInputString = "{"
                + "\"dlParamM\":{"
                + "    \"bfSpecRegNo\":\""+orderPlanNo+"\""
                + "}}";

        String cookies = "JSESSIONID=" + sessionId
                + "; WHATAP=x342cfbfle816r"
                + "; XTVID=A2501271422049643"
                + "; Path=/"
                + "; infoSysCd=%EC%A0%95010029"
                + "; _harry_url=https%3A//www.g2b.go.kr/"
                + "; xloc=1194X834"
                + "; poupR23AB0000013499=done"
                + "; lastAccess=1740232230001";

        HttpRequest dataRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.g2b.go.kr/pn/pnz/pnza/BfSpec/selectBfSpec.do"))
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Accept", "application/json")
                .header("Referer", "https://www.g2b.go.kr/")
                .header("Cookie", cookies) // 갱신된 쿠키 포함
                .header("menu-info", "{\"menuNo\":\"01141\",\"menuCangVal\":\"PRVA004_02\",\"bsneClsfCd\":\"%EC%97%85130025\",\"scrnNo\":\"05929\"}")
                .header("submissionid", "mf_wfm_container_sbmSrch")
                .header("usr-id", "null")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();

        return client.send(dataRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 응답 객체 중 날짜 데이터가 없을 경우 null 처리
     * @param node 응답 객체 중 날짜 데이터
     * @param formatter 변환할 날짜 데이터
     * @return LocalDateTime 형식으로 변환된 날짜 데이터
     */
    private LocalDateTime parseDate(JsonNode node, DateTimeFormatter formatter) {
        try {
            String dateStr = StringEscapeUtils.unescapeHtml4(node.asText(null)); // 없으면 null 반환
            return (dateStr != null && !dateStr.isEmpty())
                    ? LocalDateTime.parse(dateStr, formatter)
                    : null;
        } catch (Exception e) {
            return null; // 변환 실패 시 null 반환
        }
    }

    /**
     * JSESSIONID 추출 메서드
     * @param headers 응답 객체 Headers
     * @return 세션 id
     */
    private static String extractJSessionId(Map<String, List<String>> headers) {
        if (headers.containsKey("set-cookie")) {
            for (String cookie : headers.get("set-cookie")) {
                if (cookie.startsWith("JSESSIONID=")) {
                    return cookie.split(";")[0].split("=")[1]; // JSESSIONID 값 추출
                }
            }
        }
        return null;
    }





}
