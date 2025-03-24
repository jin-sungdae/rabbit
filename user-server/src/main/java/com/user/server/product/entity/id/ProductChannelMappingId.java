package com.user.server.product.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductChannelMappingId implements Serializable {

    private Long productId;
    private Long channelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductChannelMappingId that)) return false;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, channelId);
    }
}
