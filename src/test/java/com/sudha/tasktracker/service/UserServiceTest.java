package com.sudha.tasktracker.service;

import com.sudha.tasktracker.model.User;
import com.sudha.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldReturnUserById() {
        User user = new User();
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        User user = new User();
        user.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(repository, times(1)).save(user);
    }

    @Test
    void shouldEncodePasswordBeforeSavingUser() {
        User user = new User();
        user.setPassword("mypassword");

        when(passwordEncoder.encode("mypassword")).thenReturn("encoded123");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("encoded123", capturedUser.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        User user = new User();
        user.setPassword(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(user)
        );

        assertEquals("Password cannot be empty", exception.getMessage());
        verify(repository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        User user = new User();
        user.setPassword("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(user)
        );

        assertEquals("Password cannot be empty", exception.getMessage());
        verify(repository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldDeleteUserById() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(repository, times(1)).deleteById(userId);
    }
}
