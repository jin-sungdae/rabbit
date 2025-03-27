package com.user.server.product.dto;

import com.user.server.brand.entity.ProductBrand;
import com.user.server.file.dto.FileDto;
import com.user.server.file.entity.File;
import com.user.server.product.entity.Product;
import lombok.*;


@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseProductDto {

    private Long productId;
    private String defaultName;
    private String productCode;
    private String productType;
    private Boolean isFeatured;
    private ProductBrand productBrand;
    private Boolean isActive;

    private FileDto mainImage;


    public static ResponseProductDto from(Product product) {

        FileDto fileDto = product.getFiles().stream()
                .filter(file -> "PRODUCT_MAIN".equals(file.getParentType()))
                .findFirst()
                .map(FileDto::from)
                .orElse(null);

        return ResponseProductDto.builder()
                .productId(product.getId())
                .defaultName(product.getDefaultName())
                .productCode(product.getProductCode())
                .productType(product.getProductType())
                .isFeatured(product.getIsFeatured())
                .productBrand(product.getBrand())
                .mainImage(fileDto)
                .build();
    }
}
