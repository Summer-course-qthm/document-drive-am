package com.document.anhminh.controller;

import com.document.anhminh.entity.FileEntity;
import com.document.anhminh.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}