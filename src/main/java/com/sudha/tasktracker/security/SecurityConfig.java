package com.sudha.tasktracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/",
          "/login.html",
          "/index.html",
          "/popup.html",
          "/api/me",
          "/css/**",
          "/js/**",
          "/images/**",
          "/h2-console/**"
        ).permitAll()
        // USER MANAGEMENT: only admin
        .requestMatchers("/api/users/**").hasRole("ADMIN")

        // TASKS:
        .requestMatchers(HttpMethod.GET, "/api/tasks/**")
          .hasAnyRole("ADMIN", "USER", "VIEWER")

        .requestMatchers(HttpMethod.POST, "/api/tasks/**")
          .hasAnyRole("ADMIN", "USER")

        .requestMatchers(HttpMethod.PUT, "/api/tasks/**")
          .hasAnyRole("ADMIN", "USER")

        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**")
          .hasAnyRole("ADMIN", "USER")

        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/login.html")     // show the static page
        .loginProcessingUrl("/login") // Spring handles POST /login
        .defaultSuccessUrl("/index.html", true)
        .failureUrl("/login.html?error=true")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login.html")
      );

    http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

    return http.build();
  }
}
