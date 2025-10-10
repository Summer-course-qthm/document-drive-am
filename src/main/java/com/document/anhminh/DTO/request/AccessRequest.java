package com.document.anhminh.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequest {
    private Integer userId;
    private Integer roleId;
    private String type; // FOLDER or FILE
    private Integer itemId;
}
