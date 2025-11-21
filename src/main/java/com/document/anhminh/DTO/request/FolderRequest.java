package com.document.anhminh.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderRequest {
    private String nameFolder;
    private Integer userId;
    private Integer collectionId;
}
