package com.nivuskorea.procurement.dto;

import com.nivuskorea.procurement.entity.ContractType;
import com.nivuskorea.procurement.entity.DetailProduct;
import com.nivuskorea.procurement.entity.ProjectSearchKeyword;
import com.nivuskorea.procurement.entity.RestrictedRegion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CategoryDTO {
    private String[] proKeywords;
    private String[] bidKeywords;
    private String[] contractMethods;
    private String[] restrictRegions;
    private String[] preDetailProducts;
    private String[] bidDetailProducts;

    public CategoryDTO() {
    }

    public CategoryDTO(List<ProjectSearchKeyword> proKeywords,List<ProjectSearchKeyword> bidKeywords, List<ContractType> contractMethods, List<RestrictedRegion> restrictRegions, List<DetailProduct> preDetailProducts, List<DetailProduct> bidDetailProducts) {
        this.proKeywords = proKeywords.stream().map(ProjectSearchKeyword::getSearchKeyword).toArray(String[]::new);
        this.bidKeywords = bidKeywords.stream().map(ProjectSearchKeyword::getSearchKeyword).toArray(String[]::new);
        this.contractMethods = contractMethods.stream().map(contractType -> contractType.getContract().getDescription())
                .toArray(String[]::new);
        this.restrictRegions = restrictRegions.stream()
                .map(restrictedRegion -> restrictedRegion.getRestrictedRegion() + ":" + restrictedRegion.getRegionNumber())
                .toArray(String[]::new);
        this.preDetailProducts = preDetailProducts.stream()
                .map(detailProduct -> detailProduct.getItemName() + ":" + detailProduct.getItemNumber())  // itemName:itemNumber 형식
                .toArray(String[]::new);
        this.bidDetailProducts = bidDetailProducts.stream()
                .map(detailProduct -> detailProduct.getItemName() + ":"+ detailProduct.getItemNumber())
                .toArray(String[]::new);
    }
}
