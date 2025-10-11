package com.document.anhminh.service;

import com.document.anhminh.entity.RoleEntity;
import com.document.anhminh.entity.UserRoleEntity;
import com.document.anhminh.repository.RoleRepository;
import com.document.anhminh.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * Lấy danh sách tất cả Role trong hệ thống
     */
    public List<RoleEntity> getAllRole() {
        return roleRepository.findAll();
    }

    /**
     * Tạo mới một Role
     */
    public RoleEntity createRole(RoleEntity role) {
        if (roleRepository.findByRoleName(role.getRoleName()).isPresent()) {
            throw new RuntimeException("Tên vai trò đã tồn tại");
        }
        return roleRepository.save(role);
    }

    /**
     * Xóa một Role khỏi hệ thống (khỏi bảng `role`)
     */
    public void deleteRoleById(Integer roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("Role không tồn tại");
        }

        List<UserRoleEntity> rolesToDelete = userRoleRepository.findAll().stream()
                .filter(ur -> ur.getRoleId().equals(roleId))
                .toList();
        userRoleRepository.deleteAll(rolesToDelete);

        roleRepository.deleteById(roleId);
    }
}