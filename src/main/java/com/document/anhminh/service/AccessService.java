package com.document.anhminh.service;


import com.document.anhminh.DTO.response.AccessDetailResponse;
import com.document.anhminh.entity.RoleEntity;
import com.document.anhminh.entity.UserEntity;
import com.document.anhminh.entity.UserRoleEntity;
import com.document.anhminh.entity.UserRoleId;
import com.document.anhminh.repository.RoleRepository;
import com.document.anhminh.repository.UserRepository;
import com.document.anhminh.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccessService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    // Cấp quyền truy cập cho user vào folder/file
    public String grantAccess(Integer userId, Integer roleId, String type, Integer itemId) {
        UserRoleEntity access = UserRoleEntity.builder()
                .userId(userId)
                .roleId(roleId)
                .type(type)
                .itemId(itemId)
                .build();
        userRoleRepository.save(access);
        return "Cấp quyền truy cập thành công" + type + itemId + "cho user " + userId;
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

//    //Lấy danh sách user + role của từng folder/file
//    public List<UserRoleEntity> getAllAccess(String type, Integer itemId) {
//        return userRoleRepository.findAll().stream()
//                .filter(r -> type.equals(r.getType()) && itemId.equals(r.getItemId()))
//                .toList();
//    }

    /**
     * Lấy danh sách chi tiết user + role của từng folder/file
     */
    public List<AccessDetailResponse> getAllAccess(String type, Integer itemId) {
        // 1. Lọc ra các quyền truy cập tương ứng
        List<UserRoleEntity> accessList = userRoleRepository.findAll().stream()
                .filter(r -> type.equals(r.getType()) && itemId.equals(r.getItemId()))
                .toList();

        // Nếu không có quyền nào, trả về danh sách rỗng ngay lập tức
        if (accessList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Lấy ra danh sách các userId duy nhất từ accessList
        List<Integer> userIds = accessList.stream()
                .map(UserRoleEntity::getUserId)
                .distinct()
                .toList();

        // 3. Truy vấn TẤT CẢ user chỉ bằng MỘT câu lệnh và đưa vào Map để tra cứu nhanh
        Map<Integer, UserEntity> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getUserId, Function.identity()));

        // 4. Chuyển đổi (map) danh sách sang Response DTO
        return accessList.stream().map(access -> {

            // Lấy user từ Map đã truy vấn sẵn
            UserEntity user = userMap.get(access.getUserId());

            // Xây dựng đối tượng response
            return AccessDetailResponse.builder()
                    .username(user != null ? user.getUsername() : "HauHiHung-TODO") // Lấy username từ map
                    .roleName(getRoleNameById(access.getRoleId())) // Lấy role name từ hàm nội bộ
                    .type(access.getType())
                    .itemId(access.getItemId())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Hàm nội bộ để chuyển đổi Role ID sang Tên (KHÔNG QUERY DATABASE)
     * Giả sử: 1=ADMIN, 2=EDITOR, 3=VIEWER. Bạn hãy sửa lại cho đúng với CSDL của bạn.
     */
    private String getRoleNameById(Integer roleId) {
        return switch (roleId) {
            case 1 -> "ADMIN";
            case 2 -> "VIEWER";
            case 3 -> "EDITOR";
            default -> "Unknown Role";
        };
    }
}
