package com.document.anhminh.controller;

import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileEntity> uploadFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("folderId") Integer folderId) {
        return ResponseEntity.ok(fileService.storeFile(file, folderId));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Integer fileId) {
        return ResponseEntity.ok(fileService.deleteFile(fileId));
    }

    @PutMapping("/{fileId}/rename")
    public ResponseEntity<FileEntity> renameFile(
            @PathVariable Integer fileId,
            @RequestBody Map<String, String> payload) {

        String newName = payload.get("newName");
        return ResponseEntity.ok(fileService.renameFile(fileId, newName));
    }
    //Get all File on Folder
    @GetMapping("/by-folder/{folderId}")
    public ResponseEntity<List<FileEntity>> getFilesByFolder(@PathVariable Integer folderId) {
        return ResponseEntity.ok(fileService.getFilesByFolder(folderId));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileId) {
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
}