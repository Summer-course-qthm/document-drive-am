package com.document.anhminh.repository;

import com.document.anhminh.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Integer> {
    //Tìm tất cả các file thuộc về một folder cụ thể bằng folderId
    List<FileEntity> findByFolderId(Integer folderId);
}