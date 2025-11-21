package com.document.anhminh.ViewController;

import com.document.anhminh.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Bỏ 'import java.io.IOException;' vì không cần nữa

@Controller
public class FileViewController {

    @Autowired private FileService fileService;

    /**
     * Xử lý việc UPLOAD FILE
     */
    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("folderId") Integer folderId,
                             // SỬA LỖI: Thêm các tham số này để redirect
                             @RequestParam("folderName") String folderName,
                             @RequestParam("collectionId") Integer collectionId,
                             RedirectAttributes redirectAttributes) {

        // SỬA LỖI: Bắt RuntimeException thay vì IOException
        try {
            fileService.storeFile(file, folderId);
            redirectAttributes.addFlashAttribute("message", "Upload file thành công!");
        } catch (RuntimeException e) { // <-- SỬA Ở ĐÂY
            redirectAttributes.addFlashAttribute("error", "Upload file thất bại: " + e.getMessage());
        }

        // SỬA LỖI: Redirect về trang folder với đầy đủ tham số
        return "redirect:/folder?id=" + folderId + "&name=" + folderName + "&collectionId=" + collectionId;
    }

    /**
     * Xử lý việc XÓA FILE
     */
    @GetMapping("/file/delete")
    public String deleteFile(@RequestParam("id") Integer fileId,
                             @RequestParam("folderId") Integer folderId,
                             // SỬA LỖI: Thêm các tham số này để redirect
                             @RequestParam("folderName") String folderName,
                             @RequestParam("collectionId") Integer collectionId,
                             RedirectAttributes redirectAttributes) {

        // SỬA LỖI: Bắt RuntimeException
        try {
            fileService.deleteFile(fileId);
            redirectAttributes.addFlashAttribute("message", "Đã xóa file!");
        } catch (RuntimeException e) { // <-- SỬA Ở ĐÂY
            redirectAttributes.addFlashAttribute("error", "Xóa file thất bại: " + e.getMessage());
        }

        // SỬA LỖI: Redirect về trang folder với đầy đủ tham số
        return "redirect:/folder?id=" + folderId + "&name=" + folderName + "&collectionId=" + collectionId;
    }
}