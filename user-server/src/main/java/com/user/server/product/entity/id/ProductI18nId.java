package com.user.server.product.entity.id;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductI18nId implements Serializable {

    private Long product;
    private String languageCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductI18nId)) return false;
        ProductI18nId that = (ProductI18nId) o;
        return Objects.equals(product, that.product) &&
                Objects.equals(languageCode, that.languageCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, languageCode);
    }
}
