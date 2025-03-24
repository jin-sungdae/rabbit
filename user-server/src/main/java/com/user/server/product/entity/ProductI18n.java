package com.user.server.product.entity;

import com.user.server.product.entity.id.ProductI18nId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_I18N")
@IdClass(ProductI18nId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductI18n {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Id
    @Column(name = "LANGUAGE_CODE", nullable = false, length = 10)
    private String languageCode;

    @Column(name = "NAME", length = 255)
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ADDITIONAL_INFO", columnDefinition = "TEXT")
    private String additionalInfo;
}
