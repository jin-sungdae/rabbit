package com.user.server.category.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryMappingId implements Serializable {

    private Long productId;
    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategoryMappingId)) return false;
        ProductCategoryMappingId that = (ProductCategoryMappingId) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }
}
