package com.document.anhminh.repository;

import com.document.anhminh.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<FolderEntity, Integer> {
    // Tìm thư mục theo tên và collection_id để kiểm tra trùng lặp
    Optional<FolderEntity> findByNameAndCollectionId(String name, Integer collectionId);

    List<FolderEntity> findByCollectionId(Integer collectionId);
}