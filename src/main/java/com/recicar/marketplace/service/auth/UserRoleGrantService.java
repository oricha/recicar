package com.recicar.marketplace.service.auth;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.entity.UserRoleGrantEntity;
import com.recicar.marketplace.repository.UserRoleGrantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Keeps {@code user_role_grants} aligned with {@link User#getRole()} for RBAC expansion.
 */
@Service
public class UserRoleGrantService {

    private final UserRoleGrantRepository userRoleGrantRepository;

    public UserRoleGrantService(UserRoleGrantRepository userRoleGrantRepository) {
        this.userRoleGrantRepository = userRoleGrantRepository;
    }

    @Transactional
    public void syncPrimaryRole(User user) {
        if (user.getId() == null) {
            return;
        }
        userRoleGrantRepository.deleteByUserId(user.getId());
        UserRole role = user.getRole();
        if (role != null) {
            userRoleGrantRepository.save(new UserRoleGrantEntity(user.getId(), role.name()));
        }
    }
}
