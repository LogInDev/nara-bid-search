package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.dto.BidInfoActiveDto;
import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.*;
import com.nivuskorea.procurement.repository.BidInfomationRepository;
import com.nivuskorea.procurement.repository.BidInformationBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
    private final ProjectSearchKeywordsService projectSearchKeywordsService;

    public List<BidInfoActiveDto> getActiveBids() {
        List<Object[]> results = bidInfomationRepository.findActiveBids();

        return results.stream().map(obj -> new BidInfoActiveDto(
                (String) obj[0], // category
                BidType.valueOf((String) obj[1]), // bidType
                (String) obj[2], // title
                (String) obj[3], // institution
                (String) obj[4], // bidNumber
                (Long) obj[5], // estimatedAmount
                obj[6] != null ? ((Timestamp) obj[6]).toLocalDateTime() : null, // announcementDate
                obj[7] != null ? ((Timestamp) obj[7]).toLocalDateTime() : null, // deadline
                (String) obj[8] // contractMethod
        )).toList();
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
     * 사전규격 - 물품 > 세품품목별 조회 결과 batchinsert
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
    /**
     * 사전규격 - 일반용역, 기술용역 > 검색어별 조회 결과 batchinsert
     * @param bids 조회 결과
     */
    @Transactional
    public void savePreStdKeywordsAllBids(List<BidInformationDto> bids) {
        log.info("savePreStdKeywordsAllBids 실행");
        List<BidInformation> bidInformationList = new ArrayList<>();
        bids.forEach(bid -> {
            ProjectSearchKeyword searchKeywordById = projectSearchKeywordsService.getSearchKeywordById(bid.getKeywordId());
            bidInformationList.add(new BidInformation(bid, searchKeywordById));
        });
        batchRepository.preStdKeywordBatchUpsert(bidInformationList);
    }

}
