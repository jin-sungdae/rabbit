package com.user.server.user.entity;

import jakarta.persistence.*;
import lombok.*;

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

}
