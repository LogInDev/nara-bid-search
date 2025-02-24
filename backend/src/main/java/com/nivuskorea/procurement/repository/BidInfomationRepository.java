package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.dto.BidInfoActiveDto;
import com.nivuskorea.procurement.entity.BidInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BidInfomationRepository extends JpaRepository<BidInformation, Long> {

    /**
     * 마감일이 지나지 않은 입찰공고 결과 전체 조회
     * @return List<BidInformation>
     */
    @Query(value = """
    SELECT category, bid_type, title, institution, 
           bid_number, estimated_amount, 
           announcement_date, deadline, contract_method
    FROM procurement.bid_information
    WHERE bid_type = 'BID_ANNOUNCEMENT' AND deadline >= current_timestamp

    UNION ALL

    SELECT category, bid_type, title, institution, 
           bid_number, estimated_amount, 
           announcement_date, deadline, contract_method
    FROM procurement.bid_information
    WHERE bid_type = 'PRE_STANDARD' and announcement_date >= current_timestamp - '1 month'::interval
    """, nativeQuery = true)
    List<Object[]> findActiveBids();





}

