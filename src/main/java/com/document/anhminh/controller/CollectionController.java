package com.document.anhminh.controller;

import com.document.anhminh.DTO.request.CollectionRequest;
import com.document.anhminh.entity.CollectionEntity;
import com.document.anhminh.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collection")
@RequiredArgsConstructor
public class CollectionController {
    @Autowired
    private final CollectionService collectionService;

    @PostMapping("/create")
    public ResponseEntity<CollectionEntity> createCollection(@RequestBody CollectionRequest request) {
        return ResponseEntity.ok(collectionService.createCollection(request));
    }


}
