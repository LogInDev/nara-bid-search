package com.nivuskorea.procurement.dto;

import com.nivuskorea.procurement.entity.BidInformation;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class BidInformationDTO {
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

//    @Builder
//    public BidInformationDTO(String category, String bidType, String title, String institution, String bidNumber, Long estimatedAmount, LocalDateTime announcementDate, LocalDateTime deadline, String contractMethod) {
//        this.category = category;
//        this.bidType = bidType;
//        this.title = title;
//        this.institution = institution;
//        this.bidNumber = bidNumber;
//        this.estimatedAmount = estimatedAmount;
//        this.announcementDate = announcementDate;
//        this.deadline = deadline;
//        this.contractMethod = contractMethod;
//    }
//
//    @Builder
//    public BidInformationDTO(String category, String bidType, String title, String institution, String bidNumber, Long estimatedAmount, LocalDateTime announcementDate, LocalDateTime deadline, String contractMethod, Long productId, Long regionId, Long contractId) {
//        this.category = category;
//        this.bidType = bidType;
//        this.title = title;
//        this.institution = institution;
//        this.bidNumber = bidNumber;
//        this.estimatedAmount = estimatedAmount;
//        this.announcementDate = announcementDate;
//        this.deadline = deadline;
//        this.contractMethod = contractMethod;
//        this.productId = productId;
//        this.regionId = regionId;
//        this.contractId = contractId;
//    }

    public static BidInformationDTO fromEntity(BidInformation entity) {
        return BidInformationDTO.builder()
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