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
        user.setUsername("navin");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("navin"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("navin");

        assertNotNull(userDetails);
        assertEquals("navin", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        verify(userRepository, times(1))
                .findByUsername("navin");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> dbUserDetailsService.loadUserByUsername("unknown")
                );

        assertEquals(
                "User not found: unknown",
                exception.getMessage()
        );
    }

    @Test
    void shouldAssignRoleUserAuthority() {

        User user = new User();
        user.setUsername("navin");
        user.setPassword("password");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("navin"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("navin");

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );
    }

    @Test
    void shouldAssignRoleAdminAuthority() {

        User user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("admin");

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
    }

    @Test
    void shouldReturnSingleAuthority() {

        User user = new User();
        user.setUsername("viewer");
        user.setPassword("password");
        user.setRole(Role.VIEWER);

        when(userRepository.findByUsername("viewer"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                dbUserDetailsService.loadUserByUsername("viewer");

        assertEquals(
                1,
                userDetails.getAuthorities().size()
        );
    }
}