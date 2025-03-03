package com.nivuskorea.procurement.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PreKeywordDTO {
    private String startDate;
    private String endDate;
    private String[] keyword;

    public PreKeywordDTO(String startDate, String endDate, String[] keyword) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.keyword = keyword;
    }
}
