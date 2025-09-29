package com.document.anhminh.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

@Entity
@Table(name = "userRole")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    private String type;

    private Integer item_id;

}
