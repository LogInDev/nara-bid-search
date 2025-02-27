package com.nivuskorea.procurement.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SearchRequest {
    private List<String> categories;
    private String startDate;
    private String endDate;
    private List<String> items;
    private List<String> searchTerms;

    // Getter만 생성 (Setter 없이 생성자 활용)
    public SearchRequest(List<String> categories, String startDate, String endDate, List<String> items, List<String> searchTerms) {
        this.categories = categories;
        this.startDate = startDate;
        this.endDate = endDate;
        this.items = items;
        this.searchTerms = searchTerms;
    }
}
