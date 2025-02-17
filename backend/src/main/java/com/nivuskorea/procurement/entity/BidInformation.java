package com.nivuskorea.procurement.entity;

import com.nivuskorea.procurement.dto.BidInformationDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "bid_information", schema = "procurement")
public class BidInformation  extends BaseEntity{

    @Id
    @Column(name = "info_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_information_seq_gen")
    @SequenceGenerator(name = "bid_information_seq_gen", sequenceName = "procurement.bid_information_seq", allocationSize = 50)
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

    public BidInformation(Long id) {
        this.id = id;
    }

    public BidInformation(BidInformationDto dto) {
        this.category = Category.fromString(dto.getCategory());
        this.bidType = BidType.fromString(dto.getBidType());
        this.title = dto.getTitle();
        this.institution = dto.getInstitution();
        this.bidNumber = dto.getBidNumber();
        this.estimatedAmount = dto.getEstimatedAmount();
        this.announcementDate = dto.getAnnouncementDate();
        this.deadline = dto.getDeadline();
        this.contractMethod = dto.getContractMethod();
    }


}
