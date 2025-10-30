package com.sudha.tasktracker.service;

import com.sudha.tasktracker.model.User;
import com.sudha.tasktracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) 
    {
        this.repository = repository;
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
        return repository.save(user);
    }

    public void deleteUser(Long id) 
    {
        repository.deleteById(id);
    }
}
