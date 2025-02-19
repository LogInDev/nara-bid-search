package com.nivuskorea.procurement.repository;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.DetailProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface DetailProductsRepository extends JpaRepository<DetailProduct, Long> {

    List<DetailProduct> findByBidType(BidType bidType);

    Optional<DetailProduct> findById(Long id);
}
