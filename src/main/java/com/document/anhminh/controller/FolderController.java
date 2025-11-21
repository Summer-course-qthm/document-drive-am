package com.document.anhminh.controller;

import com.document.anhminh.DTO.request.FolderRequest;
import com.document.anhminh.entity.FolderEntity;
import com.document.anhminh.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @PostMapping("/create")
    public ResponseEntity<FolderEntity> createFolder(@RequestBody FolderRequest request) {
        return ResponseEntity.ok(folderService.createFolder(request));
    }

    @PutMapping("/{folderId}/rename")
    public ResponseEntity<FolderEntity> renameFolder(
            @PathVariable Integer folderId,
            @RequestBody Map<String, String> payload) {
        String newName = payload.get("newName");
        return ResponseEntity.ok(folderService.renameFolder(folderId, newName));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable Integer folderId) {
        return ResponseEntity.ok(folderService.deleteFolder(folderId));
    }
    //Get All folder on Collection
    @GetMapping("/by-collection/{collectionId}")
    public ResponseEntity<List<FolderEntity>> getFoldersByCollection(@PathVariable Integer collectionId) {
        return ResponseEntity.ok(folderService.getFoldersByCollection(collectionId));
    }
}