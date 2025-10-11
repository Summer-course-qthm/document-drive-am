package com.document.anhminh.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionRequest {
    private String name;
    private Integer user_Id; // id người tạo
}