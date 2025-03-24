package com.user.server.tag.entity;

import com.user.server.product.entity.Product;
import com.user.server.tag.entity.id.ProductTagMappingId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_TAG_MAPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTagMapping {

    @EmbeddedId
    private ProductTagMappingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "TAG_ID", nullable = false)
    private Tag tag;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder = 0;
}