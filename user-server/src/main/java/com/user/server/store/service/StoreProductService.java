package com.user.server.store.service;

import com.common.config.api.exception.GeneralException;
import com.common.config.api.exception.ProductNotFoundException;
import com.user.server.product.dto.RequestProductDto;
import com.user.server.product.dto.ResponseProductDto;
import com.user.server.product.entity.Product;
import com.user.server.product.respository.ProductRepository;
import com.user.server.store.dto.ProductUpdateRequest;
import com.user.server.user.entity.SellerProfile;
import com.user.server.user.entity.User;
import com.user.server.user.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreProductService {
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Transactional
    public String registerProduct(RequestProductDto requestProductDto, User user) {
        try {
            String uid = RandomStringUtils.randomAlphanumeric(16);

            SellerProfile seller = sellerProfileRepository.findByUserUid(user.getUid())
                    .orElseThrow(() -> new GeneralException("판매자 정보가 없습니다."));


            Product product = productRepository.save(requestProductDto.toEntity(user.getId(), uid, seller));
            productRepository.save(product);

            return uid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    @Transactional(readOnly = true)
    public Page<ResponseProductDto> getAllProducts(User user, Pageable pageable) {
        Page<Product> products = productRepository.findAllByUserUid(user.getUid(), pageable);

        return products.map(ResponseProductDto::from);
    }

    public void update(Long storeId, Long productId, ProductUpdateRequest request) {
    }


    public ResponseProductDto getProductByProductId(Long productId) {
        return ResponseProductDto.from(productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException()));
    }
}
