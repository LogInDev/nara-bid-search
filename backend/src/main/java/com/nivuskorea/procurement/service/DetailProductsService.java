package com.nivuskorea.procurement.service;

import com.nivuskorea.procurement.entity.BidType;
import com.nivuskorea.procurement.entity.DetailProduct;
import com.nivuskorea.procurement.repository.DetailProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DetailProductsService {

    private final DetailProductsRepository detailProductsRepository;

    public List<DetailProduct> selectByBidType(BidType bidType) {
        return detailProductsRepository.findByBidTypeOrderByIdAsc(bidType);
    }

    public DetailProduct getDetailProductById(Long id) {
        return detailProductsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DetailProduct not found with id: " + id));
    }

    @Transactional
    public void updateIsUsed(String[] preDetailProducts, BidType bidType) {
        // preDetailProducts 배열에 포함된 productCodes
        List<String> productCodes = Arrays.asList(preDetailProducts);

        // bidType과 isUsed가 true인 모든 항목을 조회
        List<DetailProduct> detailProducts = detailProductsRepository.findByBidTypeAndIsUsedTrue(bidType);

        // isUsed가 true인 애들 중에서 preDetailProducts에 포함되지 않은 애들은 false 처리
        for (DetailProduct detailProduct : detailProducts) {
            if (!productCodes.contains(detailProduct.getItemNumber())) {
                detailProduct.updateIsUsed(false); // 포함되지 않은 애들은 false 처리
            }
        }

        // preDetailProducts에 포함된 productCodes는 isUsed를 true로 설정
        for (String productCode : productCodes) {
            Optional<DetailProduct> optionalDetailProduct = detailProductsRepository.findByBidTypeAndItemNumber(bidType, productCode);
            optionalDetailProduct.ifPresent(detailProduct -> detailProduct.updateIsUsed(true)); // 존재하면 true로 업데이트
        }
    }

    public List<DetailProduct> selectedDetailProducts(BidType bidType) {
        return detailProductsRepository.findByBidTypeAndIsUsedTrue(bidType);
    }
}
