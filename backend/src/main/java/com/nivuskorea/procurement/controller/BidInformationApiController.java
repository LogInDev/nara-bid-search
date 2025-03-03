package com.nivuskorea.procurement.controller;

import com.nivuskorea.procurement.dto.*;
import com.nivuskorea.procurement.service.BidInformationService;
import com.nivuskorea.procurement.service.NaraBidAnnApiService;
import com.nivuskorea.procurement.service.NaraProcurementApiService;
import com.nivuskorea.procurement.service.openApi.OpenApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class BidInformationApiController {

    private final BidInformationService bidInformationService;
    private final NaraProcurementApiService naraProcurementApiService;
    private final NaraBidAnnApiService naraBidAnnApiService;
    private final OpenApiService openApiService;

    @GetMapping("/all")
    public List<BidInfoActiveDTO> getAllBids(){
        List<BidInfoActiveDTO> all = bidInformationService.getActiveBids();
        log.info("/all 조회 결과 : {}",all.toString());
        return all;
    }

    @PostMapping("/search")
    public ResponseEntity<List<BidInformationDTO>> getSearch(@RequestBody SearchRequestDTO request) {

        request.convertToSearchRequestDTO();
        log.info("Search request: {}", request);

        // 사전규격 > 세부품목
        final BidProductDTO preProductReq = new BidProductDTO(request.getStartDate(), request.getEndDate(), request.getProItems().toArray(new String[0]));
        // 사전규격 > 키워드
        final PreKeywordDTO preKeywordReq = new PreKeywordDTO(request.getStartDate(), request.getEndDate(), request.getSearchTerms().toArray(new String[0]));
        final List<BidInformationDTO> preKeywordRes = naraProcurementApiService.keywordApi(preKeywordReq);
        // 입찰공고 > 세부품목
        final BidProductDTO bidProductReq = new BidProductDTO(request.getStartDate(), request.getEndDate(), request.getBidItems().toArray(new String[0]), request.getBidRegions().toArray(new String[0]), request.getBidMethods().toArray(new String[0]));
        final List<BidInformationDTO> bidInformationRes = naraBidAnnApiService.bidAnnApi(bidProductReq);

        log.info(preProductReq.toString());
        log.info(preKeywordReq.toString());
        log.info(bidProductReq.toString());


        log.info(bidInformationRes.toString());
//        final OpenApiProperties openApiProperties = new OpenApiProperties();
        // 발주 > 사전규격 > 세부품목별 조회 - /getPublicPrcureThngInfoThngPPSSrch
        // 발주 > 사전규격 > 용역별 검색어 조회 - /getPublicPrcureThngInfoServcPPSSrch
        // 입찰공고 > 물품 > 세부품목별 조회 - /getBidPblancListInfoThngPPSSrch
//        openApiService.fetchData(openApiProperties.getProcurement_url(), openApiProperties.getBid_key(),)

        return ResponseEntity.ok(bidInformationRes);
    }


    @GetMapping("/test")
    public String testApi(){

        naraBidAnnApiService.bidAnnApi();
        naraProcurementApiService.keywordApi();
        naraProcurementApiService.procurementApi();

        return "testApi - ok";
    }
}
