package com.nivuskorea.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "detail_products", schema = "procurement")
public class DetailProduct extends BaseEntity{

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detail_products_seq_gen")
    @SequenceGenerator(name = "detail_products_seq_gen", sequenceName = "procurement.detail_products_seq", allocationSize = 50)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;  // 구분

    @Column(nullable = false)
    private Integer itemNumber;     // 세부품명번호

    @Column(nullable = false)
    private String itemName;        // 세부품명

    private Boolean isUsed;     // 사용 여부

    @OneToMany(mappedBy = "detailProduct")
    private List<BidInformation> bids = new ArrayList<>();

    public DetailProduct() {
    }

    public DetailProduct(Category category, Integer itemNumber, String itemName) {
        this.category = category;
        this.itemNumber = itemNumber;
        this.itemName = itemName;
    }



}
