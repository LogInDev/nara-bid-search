package com.nivuskorea.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bid_information", schema = "procurement")
public class BidInformation  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;         // 구분
    private String bidType;          // 입찰 유형
    private String title;            // 공고명
    private String institution;      // 수요 기관
    private String bidNumber;        // 공고번호 (사전규격등록번호)
    private Long estimatedAmount;    // 기초금액 (배정예산액)

    private LocalDateTime announcementDate;  // 공고일 (공개일시)
    private LocalDateTime deadline;         // 마감일 (의견등록마감)
    private String contractMethod;          // 계약방법

}
