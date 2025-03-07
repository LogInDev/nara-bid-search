package com.nivuskorea.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Table(name = "restricted_regions", schema = "procurement")
public class RestrictedRegion extends BaseEntity{

    @Id
    @Column(name = "region_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restricted_regions_seq_gen")
    @SequenceGenerator(name = "restricted_regions_seq_gen", sequenceName = "procurement.restricted_regions_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidType bidType;  // 입찰 유형

    @Column(nullable = false)
    private String restrictedRegion;        // 제한지역

    private String regionNumber;        // 지역코드

    private Boolean isUsed;     // 사용 여부

    @OneToMany(mappedBy = "restrictedRegion")
    private List<BidInformation> bids = new ArrayList<>();

    public RestrictedRegion() {
    }

    public RestrictedRegion(BidType bidType, String restrictedRegion, String regionNumber, Boolean isUsed) {
        this.bidType = bidType;
        this.restrictedRegion = restrictedRegion;
        this.regionNumber = regionNumber;
        this.isUsed = isUsed;
    }

    public void updateIsUsed(Boolean isUsed) {
        this.isUsed = isUsed; // 엔티티에서만 사용될 메서드로 상태 변경
    }
}
