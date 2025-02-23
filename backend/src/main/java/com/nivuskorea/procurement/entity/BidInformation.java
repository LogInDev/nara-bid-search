package com.nivuskorea.procurement.entity;

import com.nivuskorea.procurement.dto.BidInformationDto;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "bid_information", schema = "procurement")
public class BidInformation  extends BaseEntity{

    @Id
    @Column(name = "info_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_information_seq_gen")
    @SequenceGenerator(name = "bid_information_seq_gen", sequenceName = "procurement.bid_information_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;         // 구분

    @Enumerated(EnumType.STRING)
    private BidType bidType;          // 입찰 유형
    private String title;            // 공고명
    private String institution;      // 수요 기관

    @Column(unique = true)
    private String bidNumber;        // 공고번호 (사전규격등록번호)
    private Long estimatedAmount;    // 기초금액 (배정예산액)

    private LocalDateTime announcementDate;  // 공고일 (공개일시)
    private LocalDateTime deadline;         // 마감일 (의견등록마감)
    private String contractMethod;          // 계약방법

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private DetailProduct detailProduct;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "region_id")
    private RestrictedRegion restrictedRegion;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contract_id")
    private ContractType contractType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "keyword_id")
    private ProjectSearchKeyword projectSearchKeyword;

    public BidInformation() {
    }

    // 입찰 공고 검색 결과
    public BidInformation(BidInformationDto dto, DetailProduct detailProduct, RestrictedRegion restrictedRegion, ContractType contractType) {
        this.category = Category.fromString(dto.getCategory());
        this.bidType = BidType.fromString(dto.getBidType());
        this.title = dto.getTitle();
        this.institution = dto.getInstitution();
        this.bidNumber = dto.getBidNumber();
        this.estimatedAmount = dto.getEstimatedAmount();
        this.announcementDate = dto.getAnnouncementDate();
        this.deadline = dto.getDeadline();
        this.contractMethod = dto.getContractMethod();
        this.detailProduct = detailProduct;  // FK 엔터티 설정
        this.restrictedRegion = restrictedRegion;  // FK 엔터티 설정
        this.contractType = contractType;  // FK 엔터티 설정
    }

    // 발주 > 사전 규격 > 물품 검색 결과
    public BidInformation(BidInformationDto dto, DetailProduct detailProduct) {
        this.category = Category.fromString(dto.getCategory());
        this.bidType = BidType.fromString(dto.getBidType());
        this.title = dto.getTitle();
        this.institution = dto.getInstitution();
        this.bidNumber = dto.getBidNumber();
        this.estimatedAmount = dto.getEstimatedAmount();
        this.announcementDate = dto.getAnnouncementDate();
        this.deadline = dto.getDeadline();
        this.contractMethod = dto.getContractMethod();
        this.detailProduct = detailProduct;  // FK 엔터티 설정
    }

    // 발주 > 사전 규격 > 일반용역, 기술용역 검색 결과
    public BidInformation(BidInformationDto dto, ProjectSearchKeyword projectSearchKeyword) {
        this.category = Category.fromString(dto.getCategory());
        this.bidType = BidType.fromString(dto.getBidType());
        this.title = dto.getTitle();
        this.institution = dto.getInstitution();
        this.bidNumber = dto.getBidNumber();
        this.estimatedAmount = dto.getEstimatedAmount();
        this.announcementDate = dto.getAnnouncementDate();
        this.deadline = dto.getDeadline();
        this.contractMethod = dto.getContractMethod();
        this.projectSearchKeyword = projectSearchKeyword;  // FK 엔터티 설정
    }


}
