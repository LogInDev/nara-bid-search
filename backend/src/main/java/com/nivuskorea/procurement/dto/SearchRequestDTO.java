package com.nivuskorea.procurement.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class SearchRequestDTO {
    private String startDate;

    private String endDate;

    private List<String> proItems;

    private List<String> bidItems;

    private List<String> bidRegions;
    private List<String> bidMethods;
    private List<String> searchTerms;

    public void convertToSearchRequestDTO() {
        this.proItems = proItems.stream()
                .map(DetailProduct::getByName)
                .collect(Collectors.toList());
        this.bidItems = bidItems.stream()
                .map(DetailProduct::getByName)
                .collect(Collectors.toList());
    }
}
