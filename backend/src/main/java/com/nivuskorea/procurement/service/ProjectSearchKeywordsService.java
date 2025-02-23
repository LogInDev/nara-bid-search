package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.ProjectSearchKeyword;
import com.nivuskorea.procurement.repository.ProjectSearchKeywordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectSearchKeywordsService {

    private final ProjectSearchKeywordsRepository projectSearchKeywordsRepository;

    public List<ProjectSearchKeyword> selectByBidType(BidType bidType) {
        return projectSearchKeywordsRepository.findByBidType(bidType);
    }

    public ProjectSearchKeyword getContractMethodById(Long id) {
        return projectSearchKeywordsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProjectSearchKeyword not found with id: " + id));
    }
}
