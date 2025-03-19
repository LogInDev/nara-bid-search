package com.nivuskorea.procurement.controller;

import com.nivuskorea.procurement.dto.*;
import com.nivuskorea.procurement.service.BidInformationService;
import com.nivuskorea.procurement.service.CategoryService;
import com.nivuskorea.procurement.service.NaraBidAnnApiService;
import com.nivuskorea.procurement.service.NaraProcurementApiService;
import com.nivuskorea.procurement.service.oauth.KakaoAuthService;
import com.nivuskorea.procurement.service.openApi.OpenApiProperties;
import com.nivuskorea.procurement.service.openApi.OpenApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    private final CategoryService categoryService;
    private final OpenApiProperties openApiProperties;
    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/all")
    public List<BidInfoActiveDTO> getAllBids(){
        List<BidInfoActiveDTO> all = bidInformationService.getActiveBids();
        log.info("/all 조회 결과 : {}",all.toString());
        return all;
    }

    /**
     * 카테고리 목록 형성
     * @param types 1: 사전규격-용역 || 2. 사전규격-물품 || 3. 입찰공고-물품
     * @return ResponseEntity<CategoryDTO>
     */
    @PostMapping("/category")
    public ResponseEntity<CategoryDTO> getCategory(@RequestBody Set<String> types) {
        log.info("type : {}",types);
        return ResponseEntity.ok(categoryService.getAllCatogory(types));
    }

    /**
     * 기본 검색 조건 수정
     * @param categoryDTO 선택된 기본 검색조건
     * @return 응답상태
     */
    @PostMapping("/updateCategory")
    public ResponseEntity<String> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("categoryDTO : {}",categoryDTO);
        return ResponseEntity.ok(categoryService.updateCategory(categoryDTO));
    }

    /**
     * 기본 검색 조건에 설정된 카테고리
     * @return CategoryDTO
     */
    @GetMapping("/selectedCategory")
    public ResponseEntity<CategoryDTO> selectedCategory() {
        CategoryDTO categoryDTO = categoryService.selectedCategory();
        return ResponseEntity.ok(categoryDTO);
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
    public String testApi() {

        naraBidAnnApiService.bidAnnApi();
        naraProcurementApiService.keywordApi();
        naraProcurementApiService.procurementApi();

        return "testApi - ok";
    }
}
