package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.ProjectSearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectSearchKeywordsRepository extends JpaRepository<ProjectSearchKeyword, Long> {

    List<ProjectSearchKeyword> findByBidType(BidType bidType);

    Optional<ProjectSearchKeyword> findById(Long id);
}
