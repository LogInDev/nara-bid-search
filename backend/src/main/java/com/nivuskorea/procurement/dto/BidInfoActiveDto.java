package com.nivuskorea.procurement.dto;

import com.nivuskorea.procurement.entity.BidType;

import java.time.LocalDateTime;

public record BidInfoActiveDto(String category
        , BidType bidType, String title, String institution
        , String bidNumber, Long estimatedAmount
        , LocalDateTime announcementDate, LocalDateTime deadline
        , String contract_method) {

}
