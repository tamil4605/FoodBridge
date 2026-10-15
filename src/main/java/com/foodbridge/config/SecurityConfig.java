package com.foodbridge.config;

import com.foodbridge.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOrg = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_RECIPIENT_ORG"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isOrg) {
                response.sendRedirect("/dashboard/organization");
            } else {
                response.sendRedirect("/dashboard/donor");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/webjars/**").permitAll()
                // Public pages
                .requestMatchers("/", "/home", "/about", "/how-it-works", "/auth/**").permitAll()
                .requestMatchers("/food", "/food/details/**").permitAll()
                .requestMatchers("/materials", "/materials/details/**").permitAll()
                .requestMatchers("/cash", "/cash/details/**").permitAll()
                .requestMatchers("/needs", "/needs/details/**").permitAll()
                // Admin pages
                .requestMatchers("/admin/**", "/api/admin/**").hasAuthority("ROLE_ADMIN")
                // Organization-specific
                .requestMatchers("/dashboard/organization/**", "/needs/create", "/needs/edit/**",
                                 "/cash/create", "/requests/my-requests").hasAuthority("ROLE_RECIPIENT_ORG")
                // Donor-specific
                .requestMatchers("/dashboard/donor/**", "/food/create", "/food/my-listings",
                                 "/food/edit/**", "/materials/create", "/materials/my-listings",
                                 "/materials/edit/**").hasAnyAuthority("ROLE_INDIVIDUAL_DONOR", "ROLE_BUSINESS_DONOR")
                // General authenticated actions
                .requestMatchers("/dashboard", "/profile/**", "/cash/donate/**",
                                 "/requests/**", "/pickups/**", "/notifications/**",
                                 "/api/notifications/**", "/feedback/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .successHandler(roleBasedAuthenticationSuccessHandler())
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }
}
