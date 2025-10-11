package com.document.anhminh.controller;

import com.document.anhminh.entity.RoleEntity;
import com.document.anhminh.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Lấy danh sách tất cả Role trong hệ thống
     */
    @GetMapping
    public ResponseEntity<List<RoleEntity>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRole());
    }

    /**
     * Tạo mới một Role
     */
    @PostMapping
    public ResponseEntity<RoleEntity> createRole(@RequestBody RoleEntity role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    /**
     * Xóa một Role khỏi hệ thống (xóa trong bảng role và các quyền liên quan)
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Integer roleId) {
        roleService.deleteRoleById(roleId);
        return ResponseEntity.ok("Đã xóa role thành công!");
    }

    // API getRolesByUser và các API gán/xóa quyền cho user đã được chuyển sang AccessController
    // để quản lý quyền truy cập trên từng file/folder cụ thể.
}