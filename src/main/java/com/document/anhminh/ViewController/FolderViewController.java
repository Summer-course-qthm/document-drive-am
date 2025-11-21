package com.document.anhminh.ViewController;

import com.document.anhminh.DTO.request.FolderRequest;
import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.service.FileService;
import com.document.anhminh.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String folderPage(@RequestParam("id") Integer folderId, Model model) {
        FolderEntity folder = folderService.getFolder(folderId);
        List<FileEntity> files = fileService.getFilesByFolder(folderId);

        model.addAttribute("danhSachFile", files);
        model.addAttribute("folderName", folder.getName());
        model.addAttribute("folderId", folderId);
        model.addAttribute("collectionId", folder.getCollection().getId());
        model.addAttribute("collectionName", folder.getCollection().getName());
        return "folder";
    }

    /**
     * Hiển thị trang "Tạo Folder mới"
     */
    @GetMapping("/folder/new")
    public String newFolderForm(@RequestParam("collectionId") Integer collectionId, Model model) {
        FolderRequest newFolder = new FolderRequest();
        newFolder.setCollectionId(collectionId); // Gán sẵn collectionId
        model.addAttribute("newFolder", newFolder);
        model.addAttribute("collectionId", collectionId);
        return "folder-form"; // Trả về file folder-form.html
    }

    /**
     * Xử lý việc TẠO FOLDER MỚI
     */
    @PostMapping("/folder/create")
    public String createFolder(@ModelAttribute FolderRequest newFolder,
                               RedirectAttributes redirectAttributes) {
        try {
            folderService.createFolder(newFolder);
            redirectAttributes.addFlashAttribute("message", "Đã tạo folder \"" + newFolder.getNameFolder() + "\"");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        // Quay lại trang collection
        return "redirect:/collection?id=" + newFolder.getCollectionId();
    }
}