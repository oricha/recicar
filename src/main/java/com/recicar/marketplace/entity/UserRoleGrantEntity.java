package com.recicar.marketplace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "user_role_grants")
@IdClass(UserRoleGrantEntity.UserRoleGrantKey.class)
public class UserRoleGrantEntity implements Serializable {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "role_name", nullable = false, length = 32)
    private String roleName;

    protected UserRoleGrantEntity() {}

    public UserRoleGrantEntity(Long userId, String roleName) {
        this.userId = userId;
        this.roleName = roleName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public static class UserRoleGrantKey implements Serializable {
        private Long userId;
        private String roleName;

        public UserRoleGrantKey() {}

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }
}
