package com.user.server.series.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeriesMappingId implements Serializable {

    private Long seriesId;
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductSeriesMappingId)) return false;
        ProductSeriesMappingId that = (ProductSeriesMappingId) o;
        return Objects.equals(seriesId, that.seriesId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seriesId, productId);
    }
}
