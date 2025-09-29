package com.document.anhminh.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "folder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    private CollectionEntity collection;
}
