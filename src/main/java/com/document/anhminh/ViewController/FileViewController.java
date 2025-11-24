package com.document.anhminh.ViewController;

import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.service.FileService;
import com.document.anhminh.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class FileViewController {

    @Autowired private FileService fileService;
    @Autowired private FolderService folderService;

    /**
     * Xử lý việc UPLOAD FILE
     */
    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("folderId") Integer folderId,
                             RedirectAttributes redirectAttributes) {

        FolderEntity folder;
        try {
            folder = folderService.getFolder(folderId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }

        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file trước khi tải lên.");
            return redirectToFolder(folder);
        }

        try {
            fileService.storeFile(file, folderId);
            redirectAttributes.addFlashAttribute("message", "Upload file thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Upload file thất bại: " + e.getMessage());
        }

        return redirectToFolder(folder);
    }

    /**
     * Xử lý việc XÓA FILE
     */
    @PostMapping("/file/delete")
    public String deleteFile(@RequestParam("fileId") Integer fileId,
                             RedirectAttributes redirectAttributes) {

        FolderEntity folder = null;

        try {
            FileEntity file = fileService.getFile(fileId);
            folder = file.getFolder();
            fileService.deleteFile(fileId);
            redirectAttributes.addFlashAttribute("message", "Đã xóa file!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Xóa file thất bại: " + e.getMessage());
            if (folder == null) {
                return "redirect:/";
            }
        }

        return redirectToFolder(folder);
    }

    @GetMapping("/file/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("id") Integer fileId) {
        FileService.FileDownloadData data = fileService.loadFileAsResource(fileId);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (data.getContentType() != null) {
            try {
                mediaType = MediaType.parseMediaType(data.getContentType());
            } catch (Exception e) {
                // Nếu không parse được, dùng mặc định
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        // Encode filename để xử lý các ký tự đặc biệt
        String encodedFilename = URLEncoder.encode(
                data.getFilename() != null ? data.getFilename() : "file",
                StandardCharsets.UTF_8
        ).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + data.getFilename() + "\"; filename*=UTF-8''" + encodedFilename)
                .body(data.getResource());
    }

    private String redirectToFolder(FolderEntity folder) {
        if (folder == null) {
            return "redirect:/";
        }
        return "redirect:/folder?id=" + folder.getId();
    }
}