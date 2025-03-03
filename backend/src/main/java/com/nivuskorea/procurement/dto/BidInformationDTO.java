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

    private Long productId;  // DetailProduct의 FK
    private Long regionId;   // RestrictedRegion의 FK
    private Long contractId;   // ContractType의 FK
    private Long keywordId;   // ProjectSearchKeyword의 FK

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
                .productId(entity.getDetailProduct() != null ? entity.getDetailProduct().getId() : null) // FK 처리
                .regionId(entity.getRestrictedRegion() != null ? entity.getRestrictedRegion().getId() : null) // FK 처리
                .contractId(entity.getContractType() != null ? entity.getContractType().getId() : null) // FK 처리
                .build();
    }

}