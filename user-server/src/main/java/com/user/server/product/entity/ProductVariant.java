package com.user.server.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT_VARIANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VARIANT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "VARIANT_CODE", nullable = false, unique = true, length = 100)
    private String variantCode;

    @Column(name = "VARIANT_NAME", length = 255)
    private String variantName;

    @Column(name = "SKU", length = 100)
    private String sku;

    @Column(name = "BARCODE", length = 100)
    private String barcode;

    @Column(name = "WEIGHT")
    private Double weight;

    @Column(name = "WIDTH")
    private Double width;

    @Column(name = "HEIGHT")
    private Double height;

    @Column(name = "LENGTH")
    private Double length;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPrice> prices = new ArrayList<>();
}