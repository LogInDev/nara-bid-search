package com.nivuskorea.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "contract_types", schema = "procurement")
public class ContractType extends BaseEntity{

    @Id
    @Column(name = "contract_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_types_seq_gen")
    @SequenceGenerator(name = "contract_types_seq_gen", sequenceName = "procurement.contract_types_seq", allocationSize = 50)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidType bidType;  // 입찰 유형

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Contract contract;     // 계약방법

    private Boolean isUsed;     // 사용 여부

    @OneToMany(mappedBy = "contractType")
    private List<BidInformation> bids = new ArrayList<>();

    public ContractType() {
    }

    public ContractType(BidType bidType, Contract contract, Boolean isUsed) {
        this.bidType = bidType;
        this.contract = contract;
        this.isUsed = isUsed;
    }
}
