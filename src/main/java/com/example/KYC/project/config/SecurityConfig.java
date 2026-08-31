package com.example.KYC.project.config;

import com.example.KYC.project.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsService userDetailsService) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

//        provider.setUserDetailsService(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                                .requestMatchers("/api/auth/**")
                                .permitAll()

                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN")

                                .requestMatchers("/{fileName:.+}")
                                .permitAll()

                                .requestMatchers("/api/kyc/**")
                                .hasRole("USER")

//                        .requestMatchers("/api/auth/**")
//                        .permitAll()
//
//                        .requestMatchers("/api/admin/**")
//                        .hasRole("ADMIN")
//
//                        // Image / PDF dekhne ke liye token nahi
//                        .requestMatchers("/api/kyc/documents/**")
//                        .permitAll()
//
//                        // Baaki KYC APIs USER ke liye

//                        .requestMatchers("/api/kyc/**")
//                        .hasRole("USER")

//                        .requestMatchers(
//                                "/api/auth/**"
//                        ).permitAll()
//
//                        .requestMatchers(
//                                "/api/admin/**"
//                        ).hasRole("ADMIN")
//
//                        .requestMatchers("/api/kyc/documents/**")
//                        .hasAnyRole("USER", "ADMIN"
//                        ).permitAll()
//
//                        .requestMatchers(
//                                "/api/kyc/**"
//                        ).hasRole("USER")

                        .anyRequest().authenticated()
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}