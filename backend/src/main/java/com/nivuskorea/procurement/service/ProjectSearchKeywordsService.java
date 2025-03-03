package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.ProjectSearchKeyword;
import com.nivuskorea.procurement.repository.ProjectSearchKeywordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectSearchKeywordsService {

    private final ProjectSearchKeywordsRepository projectSearchKeywordsRepository;

    public List<ProjectSearchKeyword> selectByBidType(BidType bidType) {
        return projectSearchKeywordsRepository.findByBidTypeAndIsUsedTrueOrderByIdDesc(bidType);
    }

    public ProjectSearchKeyword getSearchKeywordById(Long id) {
        return projectSearchKeywordsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProjectSearchKeyword not found with id: " + id));
    }

    public List<ProjectSearchKeyword> seletedKeywords() {
        return projectSearchKeywordsRepository.findByIsUsedTrueOrderByIdDesc();
    }

// isUsed를 true로 업데이트
    @Transactional
    public void updateIsUsed(String[] keywords) {
        List<ProjectSearchKeyword> byIsUsedTrue = projectSearchKeywordsRepository.findByIsUsedTrueOrderByIdDesc();
        for (ProjectSearchKeyword projectSearchKeyword : byIsUsedTrue) {
            projectSearchKeyword.updateIsUsed(false);
        }

        // keywords 배열에 있는 각 키워드에 대해 ProjectSearchKeyword를 조회하여 isUsed를 true로 설정
        for (String keyword : keywords) {
            Optional<ProjectSearchKeyword> optionalKeyword = projectSearchKeywordsRepository.findBySearchKeyword(keyword);

            if (optionalKeyword.isPresent()) {
                // 2-1. 존재하는 키워드가 있으면 isUsed를 true로 업데이트
                ProjectSearchKeyword projectSearchKeyword = optionalKeyword.get();
                projectSearchKeyword.updateIsUsed(true);
            } else {
                // 2-2. 없는 키워드는 새로운 항목으로 추가 (Upsert 처리)
                ProjectSearchKeyword newKeyword = new ProjectSearchKeyword(BidType.PRE_STANDARD,keyword, true); // 생성자에 keyword와 isUsed를 설정
                projectSearchKeywordsRepository.save(newKeyword); // 새로운 키워드 추가
            }

        }
    }
}
