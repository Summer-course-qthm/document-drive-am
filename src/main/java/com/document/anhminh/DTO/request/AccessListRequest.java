package com.document.anhminh.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessListRequest {
    private String type;   // "FOLDER" hoặc "FILE"
    private Integer itemId;
}
