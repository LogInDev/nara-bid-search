package com.nivuskorea.procurement.controller;

import com.nivuskorea.procurement.dto.BidInfoActiveDto;
import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.service.BidInformationService;
import com.nivuskorea.procurement.service.NaraBidAnnApiService;
import com.nivuskorea.procurement.service.NaraProcurementApiService;
import com.nivuskorea.procurement.service.openApi.OpenApiProperties;
import com.nivuskorea.procurement.service.openApi.OpenApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public List<BidInfoActiveDto> getAllBids(){
        List<BidInfoActiveDto> all = bidInformationService.getActiveBids();
//        log.info("/all 조회 결과 : {}",all.toString());
        return all;
    }

    @PostMapping("/defaultSearch")
    public String getDefaultSearch() {
        final OpenApiProperties openApiProperties = new OpenApiProperties();
        // 발주 > 사전규격 > 세부품목별 조회 - /getPublicPrcureThngInfoThngPPSSrch
        // 발주 > 사전규격 > 용역별 검색어 조회 - /getPublicPrcureThngInfoServcPPSSrch
        // 입찰공고 > 물품 > 세부품목별 조회 - /getBidPblancListInfoThngPPSSrch
        openApiService.fetchData(openApiProperties.getProcurement_url(), openApiProperties.getBid_key(),)
    }


    @GetMapping("/test")
    public String testApi(){

//        final String response = openApiService.fetchData().block();   // 동기방식
//        System.out.println("response = " + response);
        openApiService.fetchData().subscribe(response -> {
            System.out.println("response = " + response);
        });

//        naraBidAnnApiService.bidAnnApi();
//        naraProcurementApiService.keywordApi();
//        naraProcurementApiService.procurementApi();

        return "testApi - ok";
    }
}
