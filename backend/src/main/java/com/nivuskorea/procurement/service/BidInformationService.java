package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.BidInformation;
import com.nivuskorea.procurement.entity.ContractType;
import com.nivuskorea.procurement.entity.DetailProduct;
import com.nivuskorea.procurement.entity.RestrictedRegion;
import com.nivuskorea.procurement.repository.BidInfomationRepository;
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
    private final BidInfomationRepository bidInfomationRepository;
    private final DetailProductsService detailProductsService;
    private final RestrictedRegionService restrictedRegionService;
    private final ContractTypesService contractTypesService;

    public List<BidInformationDto> getActiveBids(){
        List<BidInformation> activeBids = bidInfomationRepository.findActiveBids();
        List<BidInformationDto> dtoActiveBids = new ArrayList<>();
        activeBids.forEach(activeBid->{
            dtoActiveBids.add(BidInformationDto.fromEntity(activeBid));
        });
        return dtoActiveBids;
    }

    /**
     * 입찰공고 - 조건별 조회 결과 batchinsert
     * @param bids 조회 결과
     */
    @Transactional
    public void saveBidAnnAllBids(List<BidInformationDto> bids) {
        log.info("saveBidAnnAllBids 실행");
        List<BidInformation> bidInformationList = new ArrayList<>();
        bids.forEach(bid -> {
            DetailProduct detailProductById = detailProductsService.getDetailProductById(bid.getProductId());
            RestrictedRegion restrictRegionById = restrictedRegionService.getRestrictRegionById(bid.getRegionId());
            ContractType contractTypeById = contractTypesService.getContractMethodById(bid.getContractId());
            bidInformationList.add(new BidInformation(bid, detailProductById, restrictRegionById, contractTypeById));
        });
        batchRepository.bidAnnBatchUpsert(bidInformationList);
    }

    /**
     * 사전규격 - 조건별 조회 결과 batchinsert
     * @param bids 조회 결과
     */
    @Transactional
    public void savePreStdAllBids(List<BidInformationDto> bids) {
        log.info("savePreStandAllBids 실행");
        List<BidInformation> bidInformationList = new ArrayList<>();
        bids.forEach(bid -> {
            DetailProduct detailProductById = detailProductsService.getDetailProductById(bid.getProductId());
            bidInformationList.add(new BidInformation(bid, detailProductById));
        });
        batchRepository.preStdBatchUpsert(bidInformationList);
    }

}
