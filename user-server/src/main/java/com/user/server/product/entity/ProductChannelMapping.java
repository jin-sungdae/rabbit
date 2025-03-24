package com.user.server.product.entity;


import com.user.server.channel.entity.SalesChannel;
import com.user.server.product.entity.id.ProductChannelMappingId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_CHANNEL_MAPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductChannelMapping {

    @EmbeddedId
    private ProductChannelMappingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("channelId")
    @JoinColumn(name = "CHANNEL_ID", nullable = false)
    private SalesChannel channel;

    @Column(name = "PRODUCT_URL", length = 1024)
    private String productUrl;
}
