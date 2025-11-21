package com.document.anhminh.ViewController;

import com.document.anhminh.DTO.request.FolderRequest;
import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.service.FileService;
import com.document.anhminh.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FolderViewController {

    @Autowired private FolderService folderService;
    @Autowired private FileService fileService;

    /**
     * TRANG FOLDER (Chi tiết)
     * Hiển thị tất cả Files trong một Folder
     */
    @GetMapping("/folder")
    public String folderPage(@RequestParam("id") Integer folderId,
                             @RequestParam("name") String folderName,
                             @RequestParam("collectionId") Integer collectionId,
                             Model model) {

        List<FileEntity> files = fileService.getFilesByFolder(folderId);
        model.addAttribute("danhSachFile", files);
        model.addAttribute("folderName", folderName);
        model.addAttribute("folderId", folderId);
        model.addAttribute("collectionId", collectionId); // Để tạo link quay lại
        return "folder";
    }

    /**
     * Hiển thị trang "Tạo Folder mới"
     */
    @GetMapping("/folder/new")
    public String newFolderForm(@RequestParam("collectionId") Long collectionId, Model model) {
        FolderRequest newFolder = new FolderRequest();
        newFolder.setCollectionId(collectionId); // Gán sẵn collectionId
        model.addAttribute("newFolder", newFolder);
        return "folder-form"; // Trả về file folder-form.html
    }

    /**
     * Xử lý việc TẠO FOLDER MỚI
     */
    @PostMapping("/folder/create")
    public String createFolder(@ModelAttribute FolderRequest newFolder) {
        folderService.createFolder(newFolder);
        // Quay lại trang collection
        return "redirect:/collection?id=" + newFolder.getCollectionId();
    }
}