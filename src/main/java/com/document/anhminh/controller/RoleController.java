package com.document.anhminh.controller;

import com.document.anhminh.DTO.request.AssignRoleRequest;
import com.document.anhminh.entity.RoleEntity;
import com.document.anhminh.entity.UserRoleEntity;
import com.document.anhminh.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

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
     * Gán Role cho User
     */
    @PostMapping("/assign")
    public ResponseEntity<String> assignRoleToUser(@RequestBody AssignRoleRequest request) {
        roleService.addRole(request.getUserId(), request.getRoleId());
        return ResponseEntity.ok("Đã gán role cho user thành công!");
    }

    /**
     * Xóa Role của User
     */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeRoleFromUser(@RequestBody AssignRoleRequest request) {
        roleService.removeRoleFromUser(request.getUserId(), request.getRoleId());
        return ResponseEntity.ok("Đã xóa role của user thành công!");
    }


    /**
     * Lấy danh sách Role của một User
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserRoleEntity>> getRolesByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(roleService.getRoleByUser(userId));
    }

    /**
     * Xóa một Role (chỉ xóa Role trong bảng role)
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Integer roleId) {
        // Nếu bạn muốn xóa role hoàn toàn khỏi bảng role (chứ không phải remove user-role)
        roleService.removeRoleFromUser(null, roleId);
        return ResponseEntity.ok("Đã xóa role thành công!");
    }
}
