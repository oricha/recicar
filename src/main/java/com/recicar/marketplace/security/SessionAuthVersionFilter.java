package com.recicar.marketplace.security;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Forces logout when {@link User#getAuthVersion()} no longer matches the session stamp
 * (e.g. after password reset from another device).
 */
public class SessionAuthVersionFilter extends OncePerRequestFilter {

    public static final String SESSION_AUTH_VERSION_ATTR = "RECICAR_AUTH_VERSION";

    private final UserRepository userRepository;

    public SessionAuthVersionFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal principal) {

            HttpSession session = request.getSession(false);
            if (session != null) {
                Optional<User> dbUser = userRepository.findById(principal.getUserId());
                if (dbUser.isEmpty()) {
                    SecurityContextHolder.clearContext();
                    session.invalidate();
                    reject(request, response);
                    return;
                }
                User u = dbUser.get();
                Object attr = session.getAttribute(SESSION_AUTH_VERSION_ATTR);
                if (attr == null) {
                    session.setAttribute(SESSION_AUTH_VERSION_ATTR, u.getAuthVersion());
                } else {
                    long sessionV = ((Number) attr).longValue();
                    if (sessionV != u.getAuthVersion()) {
                        SecurityContextHolder.clearContext();
                        session.invalidate();
                        reject(request, response);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void reject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        if (uri.contains("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            response.sendRedirect(request.getContextPath() + "/login?session=expired");
        }
    }
}
