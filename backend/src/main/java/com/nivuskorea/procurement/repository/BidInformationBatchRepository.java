package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidInformation;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidInformationBatchRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private static final String UPSERT_BID_ANN_SQL = """
        INSERT INTO procurement.bid_information (
            category, bid_type, title, institution, bid_number, estimated_amount, announcement_date, deadline, contract_method, product_id, region_id, contract_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (bid_number)
        DO UPDATE SET
            category = EXCLUDED.category,
            bid_type = EXCLUDED.bid_type,
            title = EXCLUDED.title,
            institution = EXCLUDED.institution,
            estimated_amount = EXCLUDED.estimated_amount,
            announcement_date = EXCLUDED.announcement_date,
            deadline = EXCLUDED.deadline,
            contract_method = EXCLUDED.contract_method,
            product_id = EXCLUDED.product_id,
            region_id = EXCLUDED.region_id,
            contract_id = EXCLUDED.contract_id;
    """;

    @Transactional
    public void bidAnnBatchUpsert(List<BidInformation> bidList) {
        int batchSize = 50; // 배치 사이즈 조절 가능
        for (int i = 0; i < bidList.size(); i++) {
            BidInformation bid = bidList.get(i);

            Query query = entityManager.createNativeQuery(UPSERT_BID_ANN_SQL)
                    .setParameter(1, bid.getCategory().name())
                    .setParameter(2, bid.getBidType().name())
                    .setParameter(3, bid.getTitle())
                    .setParameter(4, bid.getInstitution())
                    .setParameter(5, bid.getBidNumber())
                    .setParameter(6, bid.getEstimatedAmount())
                    .setParameter(7, bid.getAnnouncementDate())
                    .setParameter(8, bid.getDeadline())
                    .setParameter(9, bid.getContractMethod())
                    .setParameter(10, bid.getDetailProduct().getId())
                    .setParameter(11, bid.getRestrictedRegion().getId())
                    .setParameter(12, bid.getContractType().getId());

            query.executeUpdate();

            // 배치 처리
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    private static final String UPSERT_PRE_STD_SQL = """
        INSERT INTO procurement.bid_information (
            category, bid_type, title, institution, bid_number, estimated_amount, announcement_date, deadline, contract_method, product_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (bid_number)
        DO UPDATE SET
            category = EXCLUDED.category,
            bid_type = EXCLUDED.bid_type,
            title = EXCLUDED.title,
            institution = EXCLUDED.institution,
            estimated_amount = EXCLUDED.estimated_amount,
            announcement_date = EXCLUDED.announcement_date,
            deadline = EXCLUDED.deadline,
            contract_method = EXCLUDED.contract_method,
            product_id = EXCLUDED.product_id;
    """;

    @Transactional
    public void preStdBatchUpsert(List<BidInformation> bidList) {
        int batchSize = 50; // 배치 사이즈 조절 가능
        for (int i = 0; i < bidList.size(); i++) {
            BidInformation bid = bidList.get(i);

            Query query = entityManager.createNativeQuery(UPSERT_PRE_STD_SQL)
                    .setParameter(1, bid.getCategory().name())
                    .setParameter(2, bid.getBidType().name())
                    .setParameter(3, bid.getTitle())
                    .setParameter(4, bid.getInstitution())
                    .setParameter(5, bid.getBidNumber())
                    .setParameter(6, bid.getEstimatedAmount())
                    .setParameter(7, bid.getAnnouncementDate())
                    .setParameter(8, bid.getDeadline())
                    .setParameter(9, bid.getContractMethod())
                    .setParameter(10, bid.getDetailProduct().getId());

            query.executeUpdate();

            // 배치 처리
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    private static final String UPSERT_PRE_STD_KEYWORD_SQL = """
        INSERT INTO procurement.bid_information (
            category, bid_type, title, institution, bid_number, estimated_amount, announcement_date, deadline, contract_method, keyword_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (bid_number)
        DO UPDATE SET
            category = EXCLUDED.category,
            bid_type = EXCLUDED.bid_type,
            title = EXCLUDED.title,
            institution = EXCLUDED.institution,
            estimated_amount = EXCLUDED.estimated_amount,
            announcement_date = EXCLUDED.announcement_date,
            deadline = EXCLUDED.deadline,
            contract_method = EXCLUDED.contract_method,
            keyword_id = EXCLUDED.keyword_id;
    """;

    @Transactional
    public void preStdKeywordBatchUpsert(List<BidInformation> bidList) {
        int batchSize = 50; // 배치 사이즈 조절 가능
        for (int i = 0; i < bidList.size(); i++) {
            BidInformation bid = bidList.get(i);

            Query query = entityManager.createNativeQuery(UPSERT_PRE_STD_KEYWORD_SQL)
                    .setParameter(1, bid.getCategory().name())
                    .setParameter(2, bid.getBidType().name())
                    .setParameter(3, bid.getTitle())
                    .setParameter(4, bid.getInstitution())
                    .setParameter(5, bid.getBidNumber())
                    .setParameter(6, bid.getEstimatedAmount())
                    .setParameter(7, bid.getAnnouncementDate())
                    .setParameter(8, bid.getDeadline())
                    .setParameter(9, bid.getContractMethod())
                    .setParameter(10, bid.getProjectSearchKeyword().getId());

            query.executeUpdate();

            // 배치 처리
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

}

