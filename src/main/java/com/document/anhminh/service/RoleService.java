package com.document.anhminh.service;


import com.document.anhminh.entity.RoleEntity;
import com.document.anhminh.entity.UserEntity;
import com.document.anhminh.entity.UserRoleEntity;
import com.document.anhminh.entity.UserRoleId;
import com.document.anhminh.repository.RoleRepository;
import com.document.anhminh.repository.UserRepository;
import com.document.anhminh.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Tìm kiếm tất cả role
    public List<RoleEntity> getAllRole(){
        return roleRepository.findAll();
    }

    //Tạo mới role
    public RoleEntity createRole(RoleEntity role) {
        if (roleRepository.findByRoleName(role.getRoleName()).isPresent()) {
            throw new RuntimeException("Role đã tồn tại");
        }
        return roleRepository.save(role);
    }

    //Xóa role theo ID
    //rename Remove user access
    public void deleteRole (Integer userId,Integer roleId) {
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
    }

    public void deleteRoleById(Integer roleId) {
        roleRepository.deleteById(roleId);
    }

    //Cấp Role cho user
    //Rename GrantUserAccessRole
    //Item_id
    public void addRole(Integer userId, Integer roleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        //Đã lấy được user và role hiện tại -> Tạo userRoleEntity

        UserRoleEntity userRole = UserRoleEntity.builder()
                .userId(user.getUserId())
                .roleId(role.getRoleId())
                .build();
        userRoleRepository.save(userRole);
    }

    //Xóa Role của User
    //Dư thừa
    public void removeRoleFromUser(Integer userId, Integer roleId) {
        UserRoleId id = new UserRoleId();
        id.setUserId(userId);
        id.setRoleId(roleId);

        userRoleRepository.deleteById(id);
    }


    //Lấy danh sách Role của User
    //Thay đổi lại, lấy tất cả item mà user được grant access
    public List<UserRoleEntity> getRoleByUser(Integer userId){
        return userRoleRepository.findByUserId(userId);
    }







}
