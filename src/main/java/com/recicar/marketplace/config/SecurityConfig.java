package com.recicar.marketplace.config;

import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.security.SessionAuthVersionFilter;
import com.recicar.marketplace.service.CustomUserDetailsService;
import com.recicar.marketplace.service.PostLoginActionsService;
import com.recicar.marketplace.service.UserService;
import com.recicar.marketplace.service.auth.AuthLoginAuditService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${app.auth.remember-me-key:CHANGE_ME_REMEMBER_KEY}")
    private String rememberMeKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler(
            PostLoginActionsService postLoginActionsService,
            UserService userService,
            UserDetailsService userDetailsService,
            AuthLoginAuditService authLoginAuditService) {
        return new CustomAuthenticationSuccessHandler(postLoginActionsService, userService, userDetailsService, authLoginAuditService);
    }

    @Bean
    public AuthenticationFailureHandler recordingAuthenticationFailureHandler(
            UserService userService,
            AuthLoginAuditService authLoginAuditService,
            UserRepository userRepository) {
        return new RecordingAuthenticationFailureHandler("/login?error", userService, authLoginAuditService, userRepository);
    }

    @Bean
    public SessionAuthVersionFilter sessionAuthVersionFilter(UserRepository userRepository) {
        return new SessionAuthVersionFilter(userRepository);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*}") String allowedPatterns) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedPatterns.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            AuthenticationSuccessHandler customAuthenticationSuccessHandler,
            AuthenticationFailureHandler recordingAuthenticationFailureHandler,
            SessionAuthVersionFilter sessionAuthVersionFilter,
            CustomUserDetailsService userDetailsService) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; "
                                        + "script-src 'self' 'unsafe-inline' https://accounts.google.com https://connect.facebook.net; "
                                        + "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; "
                                        + "style-src-elem 'self' 'unsafe-inline' https://fonts.googleapis.com; "
                                        + "img-src 'self' data: https:; "
                                        + "font-src 'self' https://fonts.gstatic.com data:; "
                                        + "frame-ancestors 'none'; "
                                        + "connect-src 'self' https://accounts.google.com https://www.facebook.com;"
                        ))
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(Customizer.withDefaults())
                        .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .cacheControl(Customizer.withDefaults())
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**")
                )
                .addFilterAfter(sessionAuthVersionFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/",
                                "/assets/**",
                                "/static/**",
                                "/img/**",
                                "/css/**",
                                "/js/**",
                                "/api/v1/auth/**",
                                "/login",
                                "/register",
                                "/forgot-password",
                                "/reset-password",
                                "/verify-email",
                                "/oauth2/**",
                                "/error",
                                "/products/**",
                                "/shop**",
                                "/product-details**",
                                "/orders/confirmation"
                        ).permitAll()
                        .requestMatchers(
                                "/user-dashboard/**",
                                "/dashboard",
                                "/dashboard/**",
                                "/messages",
                                "/messages/**",
                                "/saved-searches",
                                "/saved-searches/**",
                                "/orders",
                                "/api/v1/user/**"
                        ).authenticated()
                        .requestMatchers("/wishlist/**", "/checkout/**", "/my-account/**").authenticated()
                        .requestMatchers("/vendor/**", "/api/vendor/**", "/api/v1/vendor/**").hasAnyRole("VENDOR", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(recordingAuthenticationFailureHandler)
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID", TokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .tokenValiditySeconds(60 * 60 * 24 * 30)
                        .key(rememberMeKey)
                        .userDetailsService(userDetailsService)
                )
                .authenticationProvider(authenticationProvider);

        return http.build();
    }
}
