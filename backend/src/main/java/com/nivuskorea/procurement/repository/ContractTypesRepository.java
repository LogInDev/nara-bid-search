package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractTypesRepository extends JpaRepository<ContractType, Long> {

    List<ContractType> findByBidType(BidType bidType);

    Optional<ContractType> findById(Long id);
}
