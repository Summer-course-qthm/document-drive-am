package com.document.anhminh.DTO.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionResponse {
    private Long id;
    private String nameCollection;
    private String ownerName; // tên user tạo collection
}
