package com.document.anhminh.repository;

import com.document.anhminh.entity.CollectionEntity;
import com.document.anhminh.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollectionRepository extends JpaRepository<CollectionEntity, Integer> {
    // Nếu muốn lấy danh sách Collection của 1 user:
    List<CollectionEntity> findByUserid(UserEntity user);

    // Nếu muốn tìm Collection theo tên:
    List<CollectionEntity> findByNameContainingIgnoreCase(String name);
}
