package com.user.server.tag.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TAG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID")
    private Long id;

    @Column(name = "TAG_NAME", unique = true, nullable = false)
    private String tagName;

}