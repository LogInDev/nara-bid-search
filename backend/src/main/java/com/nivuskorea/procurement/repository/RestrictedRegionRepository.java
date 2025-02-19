package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.RestrictedRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface RestrictedRegionRepository extends JpaRepository<RestrictedRegion, Long> {

    List<RestrictedRegion> findByBidType(BidType bidType);

    Optional<RestrictedRegion> findById(Long id);
}
