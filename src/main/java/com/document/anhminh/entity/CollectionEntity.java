package com.document.anhminh.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "collection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user_id;
}
