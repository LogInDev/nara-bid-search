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
        return restrictedRegionRepository.findByBidTypeOrderByIdAsc(bidType);
    }

    public RestrictedRegion getRestrictRegionById(Long id) {
        return restrictedRegionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RestrictRegion not found with id: " + id));
    }

    public List<RestrictedRegion> selectedRestrictedRegions() {
        return restrictedRegionRepository.findByIsUsedTrue();
    }

    @Transactional
    public void updateIsUsed(String[] restrictRegions) {
        final List<RestrictedRegion> byIsUsedTrue = restrictedRegionRepository.findByIsUsedTrue();
        for (RestrictedRegion restrictedRegion : byIsUsedTrue) {
            restrictedRegion.updateIsUsed(false);
        }

        for (String restrictRegion : restrictRegions) {
            RestrictedRegion restrictedRegion = restrictedRegionRepository.findByRestrictedRegion(restrictRegion)
                    .orElseThrow(() -> new IllegalArgumentException("RestrictRegion not found with id: " + restrictRegion));

            // isUsed 값을 true로 업데이트
            restrictedRegion.updateIsUsed(true);
        }
    }
}
