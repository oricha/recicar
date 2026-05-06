package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.UserRoleGrantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleGrantRepository extends JpaRepository<UserRoleGrantEntity, UserRoleGrantEntity.UserRoleGrantKey> {

    @Modifying
    @Query("delete from UserRoleGrantEntity g where g.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndRoleName(Long userId, String roleName);

    @Query(value = "select role_name from user_role_grants where user_id = :uid", nativeQuery = true)
    List<String> findRoleNamesByUserId(@Param("uid") Long userId);
}
