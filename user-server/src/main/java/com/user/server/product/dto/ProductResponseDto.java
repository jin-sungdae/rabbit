package com.user.server.product.dto;

import com.user.server.product.entity.Product;
import com.user.server.product.entity.ProductImage;
import com.user.server.product.entity.ProductPrice;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;


@Getter
@Builder
public class ProductResponseDto {

    private Long productId;
    private String defaultName;
    private String productCode;
    private String productType;
    private String imageUrl;
    private Integer price;
    private String brandName;

    public static ProductResponseDto from(Product product) {
        ProductImage mainImage = Optional.ofNullable(product.getImages())
                .orElse(Collections.emptyList())
                .stream()
                .filter(ProductImage::getIsMain)
                .findFirst()
                .orElse(null);

        ProductPrice currentPrice = Optional.ofNullable(product.getVariants())
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(variant -> Optional.ofNullable(variant.getPrices())
                        .orElse(Collections.emptyList())
                        .stream())
                .sorted(Comparator.comparing(ProductPrice::getStartDate).reversed())
                .findFirst()
                .orElse(null);

        return ProductResponseDto.builder()
                .productId(product.getId())
                .defaultName(product.getDefaultName())
                .productCode(product.getProductCode())
                .productType(product.getProductType())
                .imageUrl(mainImage != null ? mainImage.getImageUrl() : null)
                .price(currentPrice != null ? currentPrice.getSalePrice() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getBrandName() : null)
                .build();
    }
}
