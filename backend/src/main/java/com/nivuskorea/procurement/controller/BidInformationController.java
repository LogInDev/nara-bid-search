package com.nivuskorea.procurement.controller;

import com.nivuskorea.procurement.dto.BidInformationDto;
import com.nivuskorea.procurement.entity.BidInformation;
import com.nivuskorea.procurement.repository.BidInfomationRepository;
import com.nivuskorea.procurement.repository.BidInformationBatchRepository;
import com.nivuskorea.procurement.service.BidInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class BidInformationController {

    private final BidInformationService bidInformationService;

    @GetMapping("/all")
    public List<BidInformationDto> getAllBids(){
        List<BidInformationDto> all = bidInformationService.getActiveBids();
//        log.info("/all 조회 결과 : {}",all.toString());
        return all;
    }
}
