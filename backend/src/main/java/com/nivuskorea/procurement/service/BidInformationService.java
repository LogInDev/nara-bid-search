package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.BidInformation;
import com.nivuskorea.procurement.repository.BidInformationBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BidInformationService {

    private final BidInformationBatchRepository batchRepository;

    @Transactional
    public void saveAllBids(List<BidInformationDto> bids) {
        List<BidInformation> bidInformationList = new ArrayList<>();
        bids.forEach(bid -> {
            bidInformationList.add(new BidInformation(bid));
        });
        batchRepository.batchUpsert(bidInformationList);
    }

}
