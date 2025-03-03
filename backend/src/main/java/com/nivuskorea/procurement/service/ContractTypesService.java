package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.Contract;
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
        return contractTypesRepository.findByBidTypeOrderByIdAsc(bidType);
    }

    public ContractType getContractMethodById(Long id) {
        return contractTypesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ContractMethod not found with id: " + id));
    }

    public List<ContractType> selectedContractType() {
        return contractTypesRepository.findByIsUsedTrue();
    }

    @Transactional
    public void updateIsUsed(String[] contractMethods) {

        List<ContractType> byIsUsedTrue = contractTypesRepository.findByIsUsedTrue();
        for (ContractType contractType : byIsUsedTrue) {
            contractType.updateIsUsed(false);
        }

        for (String contractMethod : contractMethods) {
            ContractType contractType = contractTypesRepository.findByContract(Contract.fromString(contractMethod))
                    .orElseThrow(() -> new IllegalArgumentException("ContractMethod not found with contract: " + contractMethod));

            // isUsed 값을 true로 없데이트
            contractType.updateIsUsed(true);
        }
    }
}
