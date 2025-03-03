package com.nivuskorea.procurement.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.Category;

import java.time.LocalDateTime;

public record BidInfoActiveDto(
    String category,
    String bidType, // 
    String title,
    String institution,
    String bidNumber,
    Long estimatedAmount,
    LocalDateTime announcementDate,
    LocalDateTime deadline,
    String contractMethod
) {
}
