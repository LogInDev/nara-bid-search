package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BidInfomationRepository extends JpaRepository<BidInformation, Long> {

    /**
     * 마감일이 지나지 않은 입찰공고 결과 전체 조회
     * @return List<BidInformation>
     */

    @Query("SELECT b FROM BidInformation b WHERE b.deadline >= CURRENT_TIMESTAMP ")
    List<BidInformation> findActiveBids();
}

