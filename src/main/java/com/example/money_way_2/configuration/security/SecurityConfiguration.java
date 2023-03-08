package com.example.money_way_2.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    @Qualifier("customAuthenticationEntryPoint")
    @Autowired
    AuthenticationEntryPoint authEntryPoint;

    private final String path = "/api/v1/auth";

    private final String[] AUTH_WHITELIST = {

    };

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailService userDetailService;


    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()

    }
}
