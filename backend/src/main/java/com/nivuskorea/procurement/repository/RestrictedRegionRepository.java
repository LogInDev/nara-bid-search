package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.RestrictedRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestrictedRegionRepository extends JpaRepository<RestrictedRegion, Long> {

    List<RestrictedRegion> findByBidTypeOrderByIdAsc(BidType bidType);

    Optional<RestrictedRegion> findById(Long id);

    Optional<RestrictedRegion> findByRestrictedRegion(String restrictRegion);

    List<RestrictedRegion> findByIsUsedTrue();
}
