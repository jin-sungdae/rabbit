package com.user.server.product.entity;

import com.user.server.product.entity.id.ProductVariantOptionId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_VARIANT_OPTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantOption {

    @EmbeddedId
    private ProductVariantOptionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("variantId")
    @JoinColumn(name = "VARIANT_ID")
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("optionId")
    @JoinColumn(name = "OPTION_ID")
    private ProductOption option;
}
