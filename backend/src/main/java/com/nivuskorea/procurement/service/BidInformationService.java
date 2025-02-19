package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.BidInformation;
import com.nivuskorea.procurement.entity.DetailProduct;
import com.nivuskorea.procurement.entity.RestrictedRegion;
import com.nivuskorea.procurement.repository.BidInformationBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BidInformationService {

    private final BidInformationBatchRepository batchRepository;
    private final DetailProductsService detailProductsService;
    private final RestrictedRegionService restrictedRegionService;

    @Transactional
    public void saveAllBids(List<BidInformationDto> bids) {
        log.info("saveAllBids 실행");
        List<BidInformation> bidInformationList = new ArrayList<>();
        bids.forEach(bid -> {
            DetailProduct detailProductById = detailProductsService.getDetailProductById(bid.getProductId());
            RestrictedRegion restrictRegionById = restrictedRegionService.getRestrictRegionById(bid.getRegionId());
            bidInformationList.add(new BidInformation(bid, detailProductById, restrictRegionById));
        });
        batchRepository.batchUpsert(bidInformationList);
    }

}
