package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.ContractType;
import com.nivuskorea.procurement.repository.ContractMethodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractMethodsService {

    private final ContractMethodsRepository contractMethodsRepository;

    public List<ContractType> selectByBidType(BidType bidType) {
        return contractMethodsRepository.findByBidType(bidType);
    }

    public ContractType getContractMethodById(Long id) {
        return contractMethodsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ContractMethod not found with id: " + id));
    }
}
