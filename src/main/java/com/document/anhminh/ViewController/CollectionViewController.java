package com.document.anhminh.ViewController;

import com.document.anhminh.DTO.request.CollectionRequest;
import com.document.anhminh.DTO.request.FolderRequest;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.service.CollectionService;
import com.document.anhminh.service.FolderService;
import com.document.anhminh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CollectionViewController {

    @Autowired private CollectionService collectionService;
    @Autowired private FolderService folderService;
    @Autowired private UserService userService; // Để lấy thông tin user

    /**
     * TRANG COLLECTION (Chi tiết)
     * Hiển thị tất cả Folders trong một Collection
     */
    @GetMapping("/collection")
    public String collectionPage(@RequestParam("id") Integer collectionId,
                                 @RequestParam(value="name", required = false) String collectionName,
                                 Model model) {

        List<FolderEntity> folders = folderService.getFoldersByCollection(collectionId);

        String resolvedName = collectionName;
        if (resolvedName == null || resolvedName.isBlank()) {
            resolvedName = collectionService.getCollection(collectionId).getName();
        }

        FolderRequest newFolder = new FolderRequest();
        newFolder.setCollectionId(collectionId);

        model.addAttribute("danhSachFolder", folders);
        model.addAttribute("collectionName", resolvedName);
        model.addAttribute("collectionId", collectionId);
        model.addAttribute("newFolder", newFolder);
        return "collection";
    }

    /**
     * Hiển thị trang "Tạo Collection mới"
     */
    @GetMapping("/collection/new")
    public String newCollectionForm(Model model) {
        model.addAttribute("newCollection", new CollectionRequest());
        return "collection-form"; // Trả về 1 file HTML mới tên là collection-form.html
    }

    /**
     * Xử lý việc TẠO COLLECTION MỚI
     */
    @PostMapping("/collection/create")
    public String createCollection(@ModelAttribute CollectionRequest newCollection) {

        // TODO: Bạn cần một cách để lấy ID của user đang đăng nhập
        // Tạm thời fix cứng user_Id = 1 (bạn cần sửa lại logic này)
        // UserEntity currentUser = userService.getUserFromPrincipal(principal);
        // newCollection.setUser_Id(currentUser.getUserId());
        newCollection.setUser_Id(1);

        collectionService.createCollection(newCollection);
        return "redirect:/"; // Quay lại trang chủ
    }
}