package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.DetailProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetailProductsRepository extends JpaRepository<DetailProduct, Long> {

    List<DetailProduct> findByBidTypeOrderByIdAsc(BidType bidType);

    Optional<DetailProduct> findById(Long id);

    // BidType과 isUsed가 true인 모든 항목 조회
    List<DetailProduct> findByBidTypeAndIsUsedTrue(BidType bidType);

    // BidType과 ItemNumber가 일치하는 항목 조회
    Optional<DetailProduct> findByBidTypeAndItemNumber(BidType bidType, String itemNumber);

    List<DetailProduct> findByBidTypeAndItemNumberIn(BidType bidType, List<String> productCodes);
}
