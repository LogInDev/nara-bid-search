package com.nivuskorea.procurement.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BidProductDTO {
    private String startDate;
    private String endDate;

    private String[] detailProducts;
    private String[] restictRegions;
    private String[] contractMethod;

    public BidProductDTO(String startDate, String endDate, String[] detailProducts) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.detailProducts = detailProducts;
    }

    public BidProductDTO(String startDate, String endDate, String[] detailProducts, String[] restictRegions, String[] contractMethod) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.detailProducts = detailProducts;
        this.restictRegions = restictRegions;
        this.contractMethod = contractMethod;
    }
}
