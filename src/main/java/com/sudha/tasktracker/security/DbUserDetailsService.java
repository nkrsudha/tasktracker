package com.sudha.tasktracker.security;

import com.sudha.tasktracker.model.User;   
import com.sudha.tasktracker.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public DbUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User u = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    String roleName = "ROLE_" + u.getRole().name(); // ADMIN/USER/VIEWER -> ROLE_ADMIN/ROLE_USER/ROLE_VIEWER
    
    return new org.springframework.security.core.userdetails.User(
        u.getUsername(),
        u.getPassword(), 
        List.of(new SimpleGrantedAuthority(roleName))
    );
  }
}
