package com.nivuskorea.procurement.controller;

import com.nivuskorea.procurement.entity.BidInformation;
import com.nivuskorea.procurement.repository.BidInformationBatchRepository;
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

    private final BidInformationBatchRepository bidInformationRepository;

    @GetMapping("/all")
    public List<BidInformation> getAllBids(){
//        List<BidInformation> all = bidInformationRepository.findAll();
//        log.info(all.toString());
//        return all;
        return null;
    }
}
