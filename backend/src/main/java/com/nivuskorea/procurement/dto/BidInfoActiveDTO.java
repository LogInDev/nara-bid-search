package com.nivuskorea.procurement.dto;

import java.time.LocalDateTime;

public record BidInfoActiveDTO(
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
