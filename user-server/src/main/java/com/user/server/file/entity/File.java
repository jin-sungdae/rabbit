package com.user.server.file.entity;

import com.user.server.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "FILE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long id;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "UNIQUE_NAME", nullable = false, unique = true)
    private String uniqueName;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    @Column(name = "PARENT_UID", nullable = false)
    private String parentUid;

    @Column(name = "FILE_URL", nullable = false)
    private String fileUrl;

    @Column(name = "PARENT_TYPE")
    private String parentType;

    @Column(name = "FILE_EXTENSION")
    private String fileExtension;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "IS_TEMP", nullable = false)
    private boolean isTemp = false;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean getIsTemp() {
        return this.isTemp;
    }
}