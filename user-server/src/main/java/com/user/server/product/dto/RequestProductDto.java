package com.user.server.product.dto;

import com.user.server.brand.entity.ProductBrand;
import com.user.server.product.entity.Product;
import com.user.server.user.entity.SellerProfile;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class RequestProductDto {

    private Long productId;
    private String defaultName;
    private String productCode;
    private String productType;
    private Boolean isFeatured;
    private ProductBrand productBrand;
    private Boolean isActive;


    public Product toEntity(Long id, String uid, SellerProfile sellerProfile) {
        return Product.builder()
                .uid(uid)
                .productCode(productCode)
                .productType(productType)
                .defaultName(defaultName)
                .isFeatured(isFeatured)
                .sellerProfile(sellerProfile)
                .createdBy(id)
                .brand(productBrand)
                .isActive(isActive)
                .build();
    }
}