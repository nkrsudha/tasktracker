package com.sudha.tasktracker.controller;

import com.sudha.tasktracker.model.User;
import com.sudha.tasktracker.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // allows connection from any frontend
public class UserController {

    private final UserService service;

    public UserController(UserService service)
   {
        this.service = service;
   }

    @GetMapping
    public List<User> getAllUsers() 
    {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) 
    {
        return service.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public User createUser(@RequestBody User user) 
    {
        return service.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) 
    {
        service.deleteUser(id);
    }
}
