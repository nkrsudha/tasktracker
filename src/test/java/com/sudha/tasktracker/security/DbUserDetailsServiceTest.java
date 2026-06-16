package com.sudha.tasktracker.security;

import com.sudha.tasktracker.model.Role;
import com.sudha.tasktracker.model.User;
import com.sudha.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DbUserDetailsService dbUserDetailsService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        verify(userRepository, times(1))
                .findByUsername("testuser");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("missinguser"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> dbUserDetailsService.loadUserByUsername("missinguser")
                );

        assertEquals(
                "User not found: missinguser",
                exception.getMessage()
        );
    }

    @Test
    void shouldAssignRoleUserAuthority() {

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("testuser");

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );
    }

    @Test
    void shouldAssignRoleAdminAuthority() {

        User user = new User();
        user.setUsername("adminuser");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername("adminuser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("adminuser");

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
    }

    @Test
    void shouldReturnSingleAuthority() {

        User user = new User();
        user.setUsername("vieweruser");
        user.setPassword("password");
        user.setRole(Role.VIEWER);

        when(userRepository.findByUsername("vieweruser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("vieweruser");

        assertEquals(
                1,
                userDetails.getAuthorities().size()
        );
    }
}