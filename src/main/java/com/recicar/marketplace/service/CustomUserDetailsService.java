package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.UserRoleGrantRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleGrantRepository userRoleGrantRepository;

    public CustomUserDetailsService(UserRepository userRepository, UserRoleGrantRepository userRoleGrantRepository) {
        this.userRepository = userRepository;
        this.userRoleGrantRepository = userRoleGrantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<String> grantRoles = userRoleGrantRepository.findRoleNamesByUserId(user.getId());
        List<String> roleNames = grantRoles.isEmpty() ? List.of(user.getRole().name()) : grantRoles;

        return new CustomUserPrincipal(user, roleNames);
    }

    public static class CustomUserPrincipal implements UserDetails {
        private final User user;
        private final List<String> roleNames;

        public CustomUserPrincipal(User user, List<String> roleNames) {
            this.user = user;
            this.roleNames = roleNames;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return roleNames.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
        }

        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            if (!user.isActive()) {
                return false;
            }
            return user.getLockedUntil() == null || !user.getLockedUntil().isAfter(LocalDateTime.now());
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.isActive() && user.isEmailVerified();
        }

        public User getUser() {
            return user;
        }

        public Long getUserId() {
            return user.getId();
        }

        public String getFullName() {
            return user.getFullName();
        }
    }
}
