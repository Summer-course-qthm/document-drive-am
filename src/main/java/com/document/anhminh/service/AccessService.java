package com.document.anhminh.service;


import com.document.anhminh.entity.UserEntity;
import com.document.anhminh.entity.UserRoleEntity;
import com.document.anhminh.entity.UserRoleId;
import com.document.anhminh.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    // Cấp quyền truy cập cho user vào folder/file
    public String grantAccess (Integer userId, Integer roleId, String type, Integer itemId) {
        UserRoleEntity access = UserRoleEntity.builder()
                .userId(userId)
                .roleId(roleId)
                .type(type)
                .itemId(itemId)
                .build();
        userRoleRepository.save(access);
        return "Cấp quyền truy cập thành công" + type  + itemId + "cho user " + userId;
    }

    //Xóa quyền truy cập
    public String removeAccess(Integer userId, String type, Integer itemId) {
        List<UserRoleEntity> list = userRoleRepository.findByUserId(userId).stream()
                .filter(r -> type.equals(r.getType()) && itemId.equals(r.getItemId()))
                .toList();

        if (list.isEmpty()) throw new RuntimeException("Không tìm thấy quyền này!");

        list.forEach(r -> userRoleRepository.deleteById(new UserRoleId(r.getUserId(), r.getRoleId())));
        return "🗑️ Đã gỡ quyền truy cập " + type + " #" + itemId + " của user " + userId;
    }

    //Lấy danh sách user + role của từng folder/file
    public List<UserRoleEntity> getAllAccess(String type, Integer itemId) {
        return userRoleRepository.findAll().stream()
                .filter(r -> type.equals(r.getType()) && itemId.equals(r.getItemId()))
                .toList();
    }
}
