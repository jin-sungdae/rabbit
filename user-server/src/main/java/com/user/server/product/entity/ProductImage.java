package com.user.server.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCT_IMAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "IMAGE_URL", nullable = false, length = 1024)
    private String imageUrl;

    @Column(name = "ALT_TEXT", length = 255)
    private String altText;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder = 0;

    @Column(name = "IS_MAIN")
    private Boolean isMain = false;

    @Column(name = "CREATED_AT", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
