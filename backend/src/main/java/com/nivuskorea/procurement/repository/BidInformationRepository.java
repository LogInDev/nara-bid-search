package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidInformationRepository extends JpaRepository<BidInformation, Long> {
}
