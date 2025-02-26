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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaraBidAnnApiService {

    private final DetailProductsService detailProductsService;
    private final RestrictedRegionService restrictedRegionService;
    private final BidInformationService bidInformationService;
    private final ContractTypesService contractTypesService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    HttpClient client = HttpClient.newHttpClient();


    public void downloadFiles(){
        try{
            final String sessionId = getSessionId();
            // 요청 객체 생성
//            String payloadData = "k00=a2MMYzianzwExorC2swMQwxC2sxMgxjMDk0MjgwNjcxODg0ZDhhYWM2MzM2MmRjMjNkNmRkYwtrMDUMMAtrMjYML2RhdGEvQXR0YWNoZmlsZXMvcHJ2YS8yMDI1LzAyLzEwLzg4ZmY1ZjY3OTdmYjQ3MDZiNWVhMDM1YWY4ZGM3MjA0Lmh3cAtrMzEMMi4g7KeA7Lmo7IScLmh3cAtrMjEMZDU4ZjllMzUtZTIwOC00NThlLThmMTctYWM1MGRiYzFlOWViLDE%3D&X-CSRF-Token=";

            String no = "21";
            String base64Encoded = Base64.getEncoder().encodeToString(no.getBytes(StandardCharsets.UTF_8)).replace("=", "");  // '=' 제거;
            no = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);

            // **파일 관련 정보**
            String untyAtchFileNo = "d58f9e35-e208-458e-8f17-ac50dbc1e9eb";
            base64Encoded = Base64.getEncoder().encodeToString(untyAtchFileNo.getBytes(StandardCharsets.UTF_8));
            untyAtchFileNo = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
            untyAtchFileNo = no +"M"+ untyAtchFileNo;
            System.out.println("untyAtchFileNo = " + untyAtchFileNo);

            no = "26";
            base64Encoded = Base64.getEncoder().encodeToString(no.getBytes(StandardCharsets.UTF_8)).replace("=", "");  // '=' 제거;
            no = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);

            String atchFilePathNm = "/data/Attachfiles/prva/2025/02/10/88ff5f6797fb4706b5ea035af8dc7204.hwp";
            base64Encoded = Base64.getEncoder().encodeToString(atchFilePathNm.getBytes(StandardCharsets.UTF_8)).replace("==", "");  // '=' 제거;
            atchFilePathNm = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
            atchFilePathNm = no + "M" + atchFilePathNm;
            System.out.println("atchFilePathNm = " + atchFilePathNm);

            no = "31";
            base64Encoded = Base64.getEncoder().encodeToString(no.getBytes(StandardCharsets.UTF_8)).replace("=", "");  // '=' 제거;
            no = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);

            String orgnlAtchFileNm = "2. 지침서.hwp";
            base64Encoded = Base64.getEncoder().encodeToString(orgnlAtchFileNm.getBytes(StandardCharsets.UTF_8)).replace("==", "");  // '=' 제거;
            orgnlAtchFileNm = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
            orgnlAtchFileNm = no + "M" + orgnlAtchFileNm;
            System.out.println("orgnlAtchFileNm = " + orgnlAtchFileNm);

            byte[] bytes = hexStringToByteArray("764d06af84f49ecaadc88e52105fddf");

            String base64 = Base64.getEncoder().encodeToString(bytes);
            System.out.println("해독핻고base64 = " + base64);

            //+ k01 1 k12 1bcabc0e76724017b034b47b6f769193
//            String encodedStr = "ExorC2swMQwxC2sxMgwxYmNhYmMwZTc2NzI0MDE3YjAzNGI0N2I2Zjc2OTE5Mw";
            String encodedStr = "NzY0ZDA2YWY4NGY0OWVjYWFkYzg4ZTUyMTA1ZmRkZg";

            // Base64 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(encodedStr);

            // 사용할 인코딩 목록
            List<Charset> encodings = Arrays.asList(
                    StandardCharsets.UTF_8,
                    Charset.forName("EUC-KR"),
                    Charset.forName("MS949"),
                    Charset.forName("ISO-8859-1")
            );

            // 여러 인코딩으로 변환 시도
            for (Charset encoding : encodings) {
                try {
                    String decodedStr = new String(decodedBytes, encoding);
                    System.out.println("Encoding (" + encoding.displayName() + "): " + decodedStr);
                } catch (Exception e) {
                    System.out.println("Encoding (" + encoding.displayName() + "): 디코딩 실패");
                }
            }
//            String atchFileNm = "88ff5f6797fb4706b5ea035af8dc7204.hwp";
//            base64Encoded = Base64.getEncoder().encodeToString(atchFileNm.getBytes(StandardCharsets.UTF_8));
//            atchFileNm = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
//            System.out.println("atchFileNm = " + atchFileNm);


            String extraParam = ",1";  // 추가 인자
            base64Encoded = Base64.getEncoder().encodeToString(extraParam.getBytes(StandardCharsets.UTF_8));
            extraParam = URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
            System.out.println("extraParam = " + extraParam);

            // ✅ **Base64 인코딩할 데이터 순서를 원래 요청과 동일하게 조정**
            String urlEncodedData = String.join("",
                    "a2MMYzianzw" +
                            "ExorC2swMQwxC2sxMgwxYmNhYmMw",
                    "ZTc2NzI0MDE3YjAzNGI0N2I2Zjc2OTE5Mw",
                    "trMDUMMA",
                    "tr" + atchFilePathNm,
                    "tr" + orgnlAtchFileNm,
                    "tr" + untyAtchFileNo + "" + extraParam
            );

            // **최종 요청 데이터**
            String payloadData = "k00=" + urlEncodedData + "&X-CSRF-Token=";

            System.out.println("payloadData = " + payloadData);

            String cookies = "JSESSIONID=" + sessionId
                    + "; WHATAP=x342cfbfle816r"
                    + "; XTVID=A2501271422049643"
                    + "; Path=/"
                    + "; infoSysCd=%EC%A0%95010029"
                    + "; _harry_fid=hh2122029691"
                    + "; XTSID=A250222215618811067"
                    + "; xloc=1194X834"
                    + "; lastAccess=1740307145884";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.g2b.go.kr/fs/fsc/fsca/fileUpload.do"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .header("Referer", "https://www.g2b.go.kr/")
                    .header("Cookie", cookies)
                    .POST(HttpRequest.BodyPublishers.ofString(payloadData))
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());


            if (response.statusCode() == 200) {
                // 파일 다운로드
                String filePath = orgnlAtchFileNm; // 저장할 파일명 설정
                try (InputStream inputStream = response.body();
                     OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("파일 다운로드 완료: " + filePath);
            } else {
                System.out.println("파일 다운로드 실패. 응답 코드: " + response.statusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 발주 목록 > 사전규격 > 일반용역, 기술용역 > 검색어 결과 저장 process
     */
    public void bidAnnApi(){
        try {
            final List<RestrictedRegion> restrictedRegions = restrictedRegionService.selectByBidType(BidType.BID_ANNOUNCEMENT);
            final List<DetailProduct> detailProducts = detailProductsService.selectByBidType(BidType.BID_ANNOUNCEMENT);
            final List<ContractType> contractTypes = contractTypesService.selectByBidType(BidType.BID_ANNOUNCEMENT);
            List<BidInformationDto> bidList = new ArrayList<>();

            final String sessionId = getSessionId();

            for (RestrictedRegion restrictedRegion : restrictedRegions) {
                for (ContractType contractType : contractTypes) {
                    for (DetailProduct detailProduct : detailProducts) {
                        getOrderPlanNoList(sessionId, detailProduct, contractType, restrictedRegion, bidList);
                    }
                }
            }
            log.info("bidList : {}", bidList);

            bidInformationService.saveBidAnnAllBids(bidList);

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
     * @param contractType 계약방법('물품' 검색 시)
     * @param restrictedRegion 참가제한지역('물품' 검색 시)
     * @return 검색 결과 중 공고 번호 리스트로 반환
     */
    private void getOrderPlanNoList(String sessionId, DetailProduct detailProduct, ContractType contractType, RestrictedRegion restrictedRegion, List<BidInformationDto> bidList) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        // 참가제한지역 설정
        String restrictedRegionCode = restrictedRegion.getRestrictedRegion().equals("인천광역시") ? "28" : "00";
        // 계약방법 설정
        String contractTypeCode = contractType.getContract() == Contract.OPEN_COMPETITION ? "계030001" : "계030003";
        log.info("restrictedRegionCode: " + restrictedRegionCode);
        log.info("contractTypeCode: " + contractTypeCode);
        final HttpRequest dataRequest = buildOrderRequest(sessionId, detailProduct.getItemNumber(), contractTypeCode, restrictedRegionCode);
        // 요청 객체로 세부 품목별 사전규격 검색 결과 응답 객체 반환
        HttpResponse<String> dataResponse = client.send(dataRequest, HttpResponse.BodyHandlers.ofString());

//        log.info(dataResponse.body());
        // 응답 객체 처리 - 검색 결과 중 공고 번호만 추출
        JsonNode rootNode = objectMapper.readTree(dataResponse.body());
        if (rootNode.has("result")) {
            for (JsonNode item : rootNode.get("result")) {
//                log.info("item : {}", item);
                if (!item.get("pbancSttsNm").asText().contains("취소")){
                    final String dataAll = StringEscapeUtils.unescapeHtml4(item.get("pbancPstgDt").asText(""));
                    final StartToEndDates dates = extractDates(dataAll);
                        bidList.add(BidInformationDto.builder()
                                .category(item.get("prcmBsneSeCdNm").asText(""))    // 구분
                                .bidType("입찰공고")
                                .title(StringEscapeUtils.unescapeHtml4(item.get("bidPbancNm").asText("")))    // 공고명
                                .institution(StringEscapeUtils.unescapeHtml4(item.get("dmstNm").asText(""))) // 수요기관
                                .bidNumber(StringEscapeUtils.unescapeHtml4(item.get("bidPbancUntyNo").asText(""))) // 공고번호
                                .estimatedAmount(item.get("alotBgtAmt").asLong(0L)) // 기초금액(배정예산액)
                                .announcementDate(dates.announcementDate) // 공고일(공개일시)
                                .deadline(dates.deadline) // 마감일(의견등록마감)
                                .contractMethod(StringEscapeUtils.unescapeHtml4(item.get("scsbdMthdNm").asText("")))
                                .productId(detailProduct.getId())
                                .regionId(restrictedRegion.getId())
                                .contractId(contractType.getId())
                                .build());
                    }
                }
            }

    }

    /**
     * 일시를 나타내는 2개의 데이터가 ()로 구분되어 같이 있는 경우 분리하는 기능
     * @param dateAll LocalDateTime1(LocalDateTmie2
     * @return LocalDateTime1, LocalDateTime2
     */
    private StartToEndDates extractDates(String dateAll) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        // <br/> 태그 제거
        dateAll = dateAll.replace("<br/>", "").trim();

        String date1 = dateAll.split("\\(")[0].trim();
        String date2 = dateAll.split("\\(")[1].split("\\)")[0].trim();
        LocalDateTime announcementDate = LocalDateTime.parse(date1, formatter);
        LocalDateTime deadline = LocalDateTime.parse(date2, formatter);

        return new StartToEndDates(announcementDate, deadline);
    }
    /**
     * extractDates()의 분리된 결과를 담는 record
     * @param announcementDate 첫번째 LocalDateTime
     * @param deadline 두번째 LocalDateTime
     */
    private record StartToEndDates(LocalDateTime announcementDate, LocalDateTime deadline) {}



/**
     * 세부품목별 사전규격 검색 결과 리스트 응답을 위한 요청객체 생성
     * @param sessionId  요청에 보낼 세션 id
     * @param itemNumber 세부품목번호
     * @param contractMethod 일반경쟁 : "계030001" || 제한경쟁 : "계030003"
     * @param restrictedRegionCode 전국(제한없음 : "00" || 인천광역시 : "28"
     * @return 요청객체
     */
    private static HttpRequest buildOrderRequest(String sessionId, String itemNumber, String contractMethod, String restrictedRegionCode) throws IOException, InterruptedException {
        // 요청일자 setting
        LocalDate today = LocalDate.now();
        final LocalDate oneMonthAgo = today.minusMonths(1);
        final LocalDate lastYearDec = today.minusYears(1).withMonth(12);
        final LocalDate thisYearDec = today.withMonth(12);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyMMdd");
        final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

        // 요청 객체 생성
        Map<String, Object> dlBidPbancLstM = new HashMap<>();
        dlBidPbancLstM.put("bidCtrtMthdCd", contractMethod);  // 계약방법
        dlBidPbancLstM.put("bidDateType", "R");
        dlBidPbancLstM.put("bsneAllYn", "Y");
        dlBidPbancLstM.put("contxtSeCd", "콘010006");
        dlBidPbancLstM.put("dtlsPrnmNo", itemNumber);  // 세부품목번호
        dlBidPbancLstM.put("frcpYn", "Y");
        dlBidPbancLstM.put("infoSysCd", "정010029");
        dlBidPbancLstM.put("fromBidDt", oneMonthAgo.format(formatter));    // 오늘일자 기준 한달 전
        dlBidPbancLstM.put("toBidDt", today.format(formatter));  // 오늘 일자
        dlBidPbancLstM.put("laseYn", "Y");
        dlBidPbancLstM.put("odnLmtLgdngCd", restrictedRegionCode);  // 참가제한지역
        dlBidPbancLstM.put("pbancKndCd", "공440002");
        dlBidPbancLstM.put("prcmBsneSeCd", "조070001");
        dlBidPbancLstM.put("recordCountPerPage", "100"); // 불러올 결과 index 수(?)
        dlBidPbancLstM.put("endIndex", 100);    // 불러올 결과 index 수
        dlBidPbancLstM.put("rsrvYn", "Y");
        dlBidPbancLstM.put("srchTy", "0");
        dlBidPbancLstM.put("startIndex", "1");  // 시작 인덱스

//        log.info("dlBidPbancLstM : {}", dlBidPbancLstM);

        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("dlBidPbancLstM", dlBidPbancLstM);

        String jsonInputString = convertToJson(jsonRequest);

        String cookies = "JSESSIONID=" + sessionId
                + "; WHATAP=x342cfbfle816r"
                + "; XTVID=A2501271422049643"
                + "; Path=/"
                + "; infoSysCd=%EC%A0%95010029"
                + "; _harry_url=https%3A//www.g2b.go.kr/"
                + "; XTSID=A250222215618811067"
                + "; xloc=1194X834"
                + "; lastAccess=1740232230001";

        HttpRequest dataRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.g2b.go.kr/pn/pnp/pnpe/BidPbac/selectBidPbacScrollTypeList.do"))
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Accept", "application/json")
                .header("Referer", "https://www.g2b.go.kr/")
                .header("Cookie", cookies) // 갱신된 쿠키 포함
                .header("menu-info", "{\"menuNo\":\"01175\",\"menuCangVal\":\"PNPE001_01\",\"bsneClsfCd\":\"%EC%97%85130026\",\"scrnNo\":\"00941\"}")
                .header("submissionid", "mf_wfm_container_tacBidPbancLst_contents_tab2_body_sbmPbancBidPbancLst")
                .header("target-id", "btnS0004")
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
