package com.user.server.channel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SALES_CHANNEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHANNEL_ID")
    private Long id;

    @Column(name = "CHANNEL_NAME", nullable = false, length = 255)
    private String name;

    @Column(name = "IS_ONLINE", nullable = false)
    private boolean isOnline = true;

    @Column(name = "BASE_URL", length = 512)
    private String baseUrl;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}