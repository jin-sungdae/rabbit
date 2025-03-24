package com.user.server.category.entity;


import com.user.server.category.entity.id.ProductCategoryMappingId;
import com.user.server.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_CATEGORY_MAPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryMapping {

    @EmbeddedId
    private ProductCategoryMappingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private ProductCategory category;

    @Column(name = "IS_DISPLAY", nullable = false)
    private boolean isDisplay = true;
}
