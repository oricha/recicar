package com.recicar.marketplace.config;

import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.service.CustomUserDetailsService;
import com.recicar.marketplace.service.PostLoginActionsService;
import com.recicar.marketplace.service.UserService;
import com.recicar.marketplace.service.auth.AuthLoginAuditService;
import com.recicar.marketplace.security.SessionAuthVersionFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

/**
 * After successful login: merge guest cart/wishlist, honor saved request, role-based landing,
 * session auth stamp for {@link SessionAuthVersionFilter}.
 */
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final PostLoginActionsService postLoginActionsService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthLoginAuditService authLoginAuditService;

    public CustomAuthenticationSuccessHandler(
            PostLoginActionsService postLoginActionsService,
            UserService userService,
            UserDetailsService userDetailsService,
            AuthLoginAuditService authLoginAuditService) {
        this.postLoginActionsService = postLoginActionsService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authLoginAuditService = authLoginAuditService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Authentication effective = authentication;

        if (authentication.getPrincipal() instanceof OAuth2User oauth) {
            String email = oauth.getAttribute("email");
            if (email == null || email.isBlank()) {
                response.sendRedirect("/login?error=oauth");
                return;
            }
            String given = oauth.getAttribute("given_name");
            String family = oauth.getAttribute("family_name");
            userService.ensureOAuthUser(email, given, family);
            UserDetails details = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            token.setDetails(new WebAuthenticationDetails(request));
            effective = token;
            var context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
            context.setAuthentication(effective);
            org.springframework.security.core.context.SecurityContextHolder.setContext(context);
            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context);
        }

        if (!(effective.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal userPrincipal)) {
            log.warn("Unexpected principal after login: {}", effective.getPrincipal());
            response.sendRedirect("/login?error");
            return;
        }

        String userEmail = userPrincipal.getUsername();
        UserRole userRole = userPrincipal.getUser().getRole();
        long authVersion = userPrincipal.getUser().getAuthVersion();

        userService.resetFailedLoginsForEmail(userEmail);
        authLoginAuditService.record(true, userEmail, userPrincipal.getUserId(), request);
        postLoginActionsService.mergeGuestDataIntoUserSession(request.getSession(), userEmail);
        stampAuthVersion(request.getSession(), authVersion);

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            requestCache.removeRequest(request, response);
            response.sendRedirect(targetUrl);
            return;
        }

        String redirectUrl = switch (userRole) {
            case ADMIN -> "/admin/dashboard";
            case VENDOR -> "/vendor/dashboard";
            case CUSTOMER -> "/";
        };

        response.sendRedirect(redirectUrl);
    }

    private void stampAuthVersion(HttpSession session, long authVersion) {
        session.setAttribute(SessionAuthVersionFilter.SESSION_AUTH_VERSION_ATTR, authVersion);
    }
}
