package com.document.anhminh.controller;

import com.document.anhminh.DTO.request.AccessListRequest;
import com.document.anhminh.DTO.request.AccessRequest;
import com.document.anhminh.DTO.response.AccessDetailResponse;
import com.document.anhminh.entity.UserRoleEntity;
import com.document.anhminh.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access")

public class AccessController {
    @Autowired
    private AccessService accessService;

    //Cấp quyền
    @PostMapping("/grant")
    public ResponseEntity<String> grantAccess(@RequestBody AccessRequest request) {
        return ResponseEntity.ok(
                accessService.grantAccess(
                        request.getUserId(),
                        request.getRoleId(),
                        request.getType(),
                        request.getItemId()
                )
        );
    }

    // Xóa quyền của user
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeAccess(@RequestBody AccessRequest request) {
        return ResponseEntity.ok(
                accessService.removeAccess(
                        request.getUserId(),
                        request.getType(),
                        request.getItemId()
                )
        );
    }

    // Lấy tất cả user có quyền trên folder/file
//    @PostMapping("/list")
//    public ResponseEntity<List<UserRoleEntity>> getAccessList(
//            @RequestBody AccessListRequest request) {
//        return ResponseEntity.ok(
//                accessService.getAllAccess(request.getType(), request.getItemId())
//        );
//    }

    // Lấy tất cả user có quyền trên folder/file
    @PostMapping("/list")
    public ResponseEntity<List<AccessDetailResponse>> getAccessList(
            @RequestBody AccessListRequest request) {
        return ResponseEntity.ok(
                accessService.getAllAccess(request.getType(), request.getItemId())
        );
    }
}
