package com.user.server.tag.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductTagMappingId implements Serializable {

    private Long productId;
    private Long tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductTagMappingId)) return false;
        ProductTagMappingId that = (ProductTagMappingId) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, tagId);
    }
}
