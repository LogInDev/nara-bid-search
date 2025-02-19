package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.RestrictedRegion;
import com.nivuskorea.procurement.repository.RestrictedRegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestrictedRegionService {

    private final RestrictedRegionRepository restrictedRegionRepository;

    public List<RestrictedRegion> selectByBidType(BidType bidType) {
        return restrictedRegionRepository.findByBidType(bidType);
    }

    public RestrictedRegion getRestrictRegionById(Long id) {
        return restrictedRegionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RestrictRegion not found with id: " + id));
    }
}
