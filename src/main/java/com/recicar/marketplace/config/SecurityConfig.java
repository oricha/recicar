package com.recicar.marketplace.config;

import com.recicar.marketplace.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Duration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self';"))
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .xssProtection(xss -> xss.disable())
                .cacheControl(cache -> cache.disable())
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            )
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/home", "/products/**", "/search/**", "/api/products/**").permitAll()
                .requestMatchers("/register", "/login", "/forgot-password", "/reset-password").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/assets/**", "/static/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/test/**").permitAll()
                
                // Cart endpoints (allow both authenticated and anonymous users)
                .requestMatchers("/cart/**", "/api/cart/**").permitAll()
                
                // Customer endpoints
                .requestMatchers("/profile/**", "/orders/**", "/checkout/**").hasAnyRole("CUSTOMER", "VENDOR", "ADMIN")
                
                // Vendor endpoints
                .requestMatchers("/vendor/**", "/api/vendor/**").hasAnyRole("VENDOR", "ADMIN")
                
                // Admin endpoints
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecret")
                .tokenValiditySeconds(86400) // 24 hours
                .userDetailsService(userDetailsService)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
            );

        return http.build();
    }
}