package com.nivuskorea.procurement.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "project_search_keywords", schema = "procurement")
public class ProjectSearchKeyword extends BaseEntity{

    @Id
    @Column(name = "keyword_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_search_keywords_seq_gen")
    @SequenceGenerator(name = "project_search_keywords_seq_gen", sequenceName = "procurement.project_search_keywords_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidType bidType;  // 입찰 유형

    private String searchKeyword;     // 검색어

    private Boolean isUsed;     // 사용 여부

    @OneToMany(mappedBy = "projectSearchKeyword")
    private List<BidInformation> bids = new ArrayList<>();

    public ProjectSearchKeyword() {
    }

    public ProjectSearchKeyword(BidType bidType, String searchKeyword, Boolean isUsed) {
        this.bidType = bidType;
        this.searchKeyword = searchKeyword;
        this.isUsed = isUsed;
    }
}
