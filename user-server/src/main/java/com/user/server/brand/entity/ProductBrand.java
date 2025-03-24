package com.user.server.brand.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCT_BRAND")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BRAND_ID")
    private Long id;

    @Column(name = "BRAND_NAME", nullable = false, length = 255)
    private String brandName;

    @Column(name = "LOGO_URL", length = 1024)
    private String logoUrl;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}