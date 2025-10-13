package com.document.anhminh.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessDetailResponse {
    private String username;
    private String roleName;
    private String type;
    private Integer itemId;
}