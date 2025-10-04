package com.document.anhminh.repository;

import com.document.anhminh.entity.UserRoleEntity;
import com.document.anhminh.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    List<UserRoleEntity> findByUserId(Integer userId);

    //Vi userId đang là khóa ghép với role trong UserRoleId nên cần viết 1 phương thức delete riêng
    void deleteByUserIdAndRoleId(Integer userId, Integer roleId);
}