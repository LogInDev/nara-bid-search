package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.ContractType;
import com.nivuskorea.procurement.repository.ContractTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractTypesService {

    private final ContractTypesRepository contractTypesRepository;

    public List<ContractType> selectByBidType(BidType bidType) {
        return contractTypesRepository.findByBidType(bidType);
    }

    public ContractType getContractMethodById(Long id) {
        return contractTypesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ContractMethod not found with id: " + id));
    }
}
