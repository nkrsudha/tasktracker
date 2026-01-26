package com.sudha.tasktracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
// TEMPORARY: In-memory user for development/testing only.
// Will be replaced with database-backed authentication.

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user = User.withUsername("sudha")
        .password(passwordEncoder.encode("password"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/login.html",
          "/css/**",
          "/js/**",
          "/images/**",
          "/h2-console/**"
        ).permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/login.html")     // show the static page
        .loginProcessingUrl("/login") // Spring handles POST /login
        .defaultSuccessUrl("/", true)
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
