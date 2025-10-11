package com.document.anhminh.DTO.request;

import lombok.Data;

@Data // Lombok sẽ tự tạo getter, setter, etc.
public class CreateFileLinkRequest {
    private Integer folderId;
    private String name;
    private String type;
    private Integer size;
    private String link;
}