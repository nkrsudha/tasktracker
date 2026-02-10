package com.sudha.tasktracker.service;

import com.sudha.tasktracker.model.User;
import com.sudha.tasktracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) 
    {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder; 
    }

    public List<User> getAllUsers() 
    {
        return repository.findAll();
    }

    public Optional<User> getUserById(Long id) 
    {
        return repository.findById(id);
    }

    public User createUser(User user) 
    {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // hash password before saving
        return repository.save(user);
    }

    public void deleteUser(Long id) 
    {
        repository.deleteById(id);
    }
}
