package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.dto.CategoryDTO;
import com.nivuskorea.procurement.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final ContractTypesService contractTypesService;
    private final DetailProductsService detailProductsService;
    private final RestrictedRegionService restrictedRegionService;
    private final ProjectSearchKeywordsService projectSearchKeywordsService;

    /**
     * 프론트에 받은 타입별 카테고리 반환
     * @param types 1: 사전규격-용역 || 2: 사전규격-물품 || 3: 입찰공고-물품
     * @return 전체 카테고리 배열 객체
     */
    public CategoryDTO getAllCatogory(Set<String> types) {
        List<ProjectSearchKeyword> projectSearchKeywords = new ArrayList<>();
        List<DetailProduct> preDetailProducts = new ArrayList<>();
        List<DetailProduct> bidDetailProducts = new ArrayList<>();
        List<RestrictedRegion> restrictedRegions = new ArrayList<>();
        List<ContractType> contractTypes = new ArrayList<>();

        if (types.contains("1")) projectSearchKeywords.addAll(projectSearchKeywordsService.selectByBidType(BidType.PRE_STANDARD));

        if (types.contains("2")) preDetailProducts.addAll(detailProductsService.selectByBidType(BidType.PRE_STANDARD));


        if (types.contains("3")) {
            bidDetailProducts.addAll(detailProductsService.selectByBidType(BidType.BID_ANNOUNCEMENT));
            restrictedRegions.addAll(restrictedRegionService.selectByBidType(BidType.BID_ANNOUNCEMENT));
            contractTypes.addAll(contractTypesService.selectByBidType(BidType.BID_ANNOUNCEMENT));
        }

        return new CategoryDTO(projectSearchKeywords,contractTypes,restrictedRegions, preDetailProducts, bidDetailProducts);
    }

    public String updateCategory(CategoryDTO categoryDTO) {
        log.info("Updating category: {}", categoryDTO);
// 각 카테고리 항목들에 대해 isUsed를 true로 설정
        if (categoryDTO.getKeywords() != null) {
            projectSearchKeywordsService.updateIsUsed(categoryDTO.getKeywords());
        }
        if (categoryDTO.getContractMethods() != null) {
            contractTypesService.updateIsUsed(categoryDTO.getContractMethods());
        }
        if (categoryDTO.getRestrictRegions() != null) {
            restrictedRegionService.updateIsUsed(categoryDTO.getRestrictRegions());
        }
        if (categoryDTO.getPreDetailProducts() != null) {
            detailProductsService.updateIsUsed(categoryDTO.getPreDetailProducts(), BidType.PRE_STANDARD);
        }
        if (categoryDTO.getBidDetailProducts() != null) {
            detailProductsService.updateIsUsed(categoryDTO.getBidDetailProducts(), BidType.BID_ANNOUNCEMENT);
        }

        return "ok";
    }

    /**
     * isUsed가 ture인 카테고리
     * @return CategoryDTO
     */
    public CategoryDTO selectedCategory() {
        List<ProjectSearchKeyword> projectSearchKeywords = new ArrayList<>(projectSearchKeywordsService.seletedKeywords());
        List<DetailProduct> preDetailProducts = new ArrayList<>(detailProductsService.selectedDetailProducts(BidType.PRE_STANDARD));
        List<DetailProduct> bidDetailProducts = new ArrayList<>(detailProductsService.selectedDetailProducts(BidType.BID_ANNOUNCEMENT));
        List<RestrictedRegion> restrictedRegions = new ArrayList<>(restrictedRegionService.selectedRestrictedRegions());
        List<ContractType> contractTypes = new ArrayList<>(contractTypesService.selectedContractType());

        return new CategoryDTO(projectSearchKeywords,contractTypes,restrictedRegions, preDetailProducts, bidDetailProducts);
    }
}
