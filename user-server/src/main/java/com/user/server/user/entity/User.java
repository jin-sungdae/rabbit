package com.user.server.user.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"users\"")
@Getter
@Setter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(nullable = false)
    private String userPassword;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = true)
    private String birthday;

    @Column(nullable = true, unique = true)
    private String phoneNo;

    @Column()
    private String zipcode;

    @Column
    private String address1;

    @Column
    private String address2;

    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private String joinRoot;

    @Column(nullable = false)
    private boolean isMailing;


    @Column(nullable = false)
    private boolean isSms;

    @Column(nullable = false, updatable = false)
    private LocalDateTime regDate;

    @PrePersist
    public void prePersist() {
        this.isMailing = true;
        this.isSms = true;
        this.regDate = LocalDateTime.now();
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private SellerProfile sellerProfile;
}