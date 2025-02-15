package com.nivuskorea.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "detail_info", schema = "procurement")
public class DetailInfo extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer itemNumber;

    @Column(nullable = false)
    private String itemName;

    public DetailInfo() {
    }

    public DetailInfo(Category category, Integer itemNumber, String itemName) {
        this.category = category;
        this.itemNumber = itemNumber;
        this.itemName = itemName;
    }

    public enum Category {
        // 사전규격, 입찰공고
        PRE_STANDARD, BID_ANNOUNCEMENT
    }

}
