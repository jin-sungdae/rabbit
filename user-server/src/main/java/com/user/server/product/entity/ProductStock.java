package com.user.server.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_STOCK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STOCK_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VARIANT_ID", nullable = false)
    private ProductVariant variant;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity = 0;

    @Column(name = "SAFE_STOCK", nullable = false)
    private Integer safeStock = 0;

    @Column(name = "LOCATION_CODE", length = 100)
    private String locationCode;

    @Column(name = "UPDATED_AT", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.time.LocalDateTime updatedAt;
}
