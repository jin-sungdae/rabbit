package com.user.server.series.entity;

import com.user.server.product.entity.Product;
import com.user.server.series.entity.id.ProductSeriesMappingId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_SERIES_MAPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSeriesMapping {

    @EmbeddedId
    private ProductSeriesMappingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seriesId")
    @JoinColumn(name = "SERIES_ID", nullable = false)
    private ProductSeries series;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "SORT_ORDER", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder;
}
