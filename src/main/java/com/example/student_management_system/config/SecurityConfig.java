package com.example.student_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Public pages
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()

                        // Student access
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/courses/view/**").hasAnyRole("STUDENT", "TEACHER")

                        // Teacher access (higher privileges)
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/courses/create").hasRole("TEACHER")
                        .requestMatchers("/courses/edit/**").hasRole("TEACHER")
                        .requestMatchers("/courses/delete/**").hasRole("TEACHER")
                        .requestMatchers("/students/create").hasRole("TEACHER")
                        .requestMatchers("/students/edit/**").hasRole("TEACHER")
                        .requestMatchers("/students/delete/**").hasRole("TEACHER")
                        .requestMatchers("/departments/**").hasRole("TEACHER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                );

        return http.build();
    }
}
