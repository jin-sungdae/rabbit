package com.user.server.product.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantOptionId implements Serializable {

    private Long variantId;
    private Long optionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantOptionId)) return false;
        ProductVariantOptionId that = (ProductVariantOptionId) o;
        return Objects.equals(variantId, that.variantId) &&
                Objects.equals(optionId, that.optionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantId, optionId);
    }
}
