package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidInformationBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String UPSERT_SQL = """
        INSERT INTO procurement.bid_information (
            category, bid_type, title, institution, bid_number, estimated_amount, announcement_date, deadline, contract_method
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (bid_number)\s
        DO UPDATE SET\s
            category = EXCLUDED.category,
            bid_type = EXCLUDED.bid_type,
            title = EXCLUDED.title,
            institution = EXCLUDED.institution,
            estimated_amount = EXCLUDED.estimated_amount,
            announcement_date = EXCLUDED.announcement_date,
            deadline = EXCLUDED.deadline,
            contract_method = EXCLUDED.contract_method;
   \s""";

    public void batchUpsert(List<BidInformation> bidList) {
        jdbcTemplate.batchUpdate(UPSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                BidInformation bid = bidList.get(i);
                ps.setString(1, bid.getCategory().name());
                ps.setString(2, bid.getBidType().name());
                ps.setString(3, bid.getTitle());
                ps.setString(4, bid.getInstitution());
                ps.setString(5, bid.getBidNumber());
                ps.setObject(6, bid.getEstimatedAmount());
                ps.setObject(7, bid.getAnnouncementDate());
                ps.setObject(8, bid.getDeadline());
                ps.setString(9, bid.getContractMethod());
            }

            @Override
            public int getBatchSize() {
                return bidList.size();
            }
        });
    }

}

