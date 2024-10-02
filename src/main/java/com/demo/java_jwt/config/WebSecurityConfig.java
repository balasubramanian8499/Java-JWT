package com.demo.java_jwt.config;

import com.demo.java_jwt.util.JwtRequestFilter;
import com.demo.java_jwt.util.JwtUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jwtUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(accessManagement -> accessManagement
                        .requestMatchers("/api/v1/auth/**",
                                "/auth/login",
                                "/auth/refresh",
                                "/actuator/health",
                                "/auth/signUp",
                                "/v3/api-docs/swagger-config",
                                "/configuration",
                                "/swagger-ui/index.html",
                                "/swagger-ui/swagger-ui.css",
                                "/swagger-ui/index.css",
                                "/swagger-ui/swagger-ui-bundle.js",
                                "/swagger-ui/swagger-ui-standalone-preset.js",
                                "/swagger-ui/favicon-32x32.png",
                                "/swagger-ui/favicon-16x16.png",
                                "/swagger-resources/**",
                                "/swagger-ui/**",
                                "/v2/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                ).sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

}