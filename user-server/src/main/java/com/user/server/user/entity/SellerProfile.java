package com.user.server.user.entity;

import com.user.server.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"seller_profile\"")
@Getter
@Setter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class SellerProfile {


    @Id @GeneratedValue
    private Long id;

    private String shopName;

    private String contact;

    @Column(length = 1000)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "sellerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

}
