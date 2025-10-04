package com.document.anhminh.DTO.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListFolderResponse {
    private Long id;
    private String nameFolder;

    private Long collectionId;

    private List<FolderDetail> folderDetail;

}
