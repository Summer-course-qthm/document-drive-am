package com.document.anhminh.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRoleId implements Serializable {
    private Integer userId;
    private Integer roleId;
}
