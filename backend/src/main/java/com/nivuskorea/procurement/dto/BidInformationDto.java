package com.nivuskorea.procurement.dto;

import com.nivuskorea.procurement.entity.BidInformation;
import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BidInformationDto {
    private String category;    // 구분(물품 || 일반용역 || 기술용역)
    private String bidType;     // 구분(사전규격 || 입찰공고)
    private String title;       // 공고명
    private String institution;     // 수요기관
    private String bidNumber;       // 공고번호
    private Long estimatedAmount;       // 기초금액(배정예산액)
    private LocalDateTime announcementDate;     // 공고일(공개일시)
    private LocalDateTime deadline;     // 마감일(의견등록마감)
    private String contractMethod;      // 계약방법

    public static BidInformationDto fromEntity(BidInformation entity) {
        return BidInformationDto.builder()
                .category(entity.getCategory().getDescription()) // 한글 변환
                .bidType(entity.getBidType().getDescription())
                .title(entity.getTitle())
                .institution(entity.getInstitution())
                .bidNumber(entity.getBidNumber())
                .estimatedAmount(entity.getEstimatedAmount())
                .announcementDate(entity.getAnnouncementDate())
                .deadline(entity.getDeadline())
                .contractMethod(entity.getContractMethod())
                .build();
    }

}