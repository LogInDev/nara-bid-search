package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.DetailProduct;
import com.nivuskorea.procurement.repository.DetailProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DetailProductsService {

    private final DetailProductsRepository detailProductsRepository;

    public List<DetailProduct> selectByBidType(BidType bidType) {
        return detailProductsRepository.findByBidType(bidType);
    }

    public DetailProduct getDetailProductById(Long id) {
        return detailProductsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DetailProduct not found with id: " + id));
    }
}
