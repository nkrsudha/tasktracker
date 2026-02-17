package com.sudha.tasktracker.model;

import jakarta.persistence.*;
import com.sudha.tasktracker.model.Role;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")  
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; 

    // One user can have many tasks
    @OneToMany(mappedBy = "assignedUser", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"assignedUser"}) // to prevent recursion during serialization
    private List<Task> tasks = new ArrayList<>();

    // Getters and Setters
    public Long getId() 
    {
         return id; 
    }
    public void setId(Long id)
    {
         this.id = id; 
    }

    public String getUsername() 
    { 
        return username; 
    }
    public void setUsername(String username) 
    { 
        this.username = username; 
    }

    public String getEmail() 
    { 
       return email; 
    }
    public void setEmail(String email) 
    { 
        this.email = email; 
    }

    public String getPassword() 
    { 
        return password;
    }
    public void setPassword(String password) 
    { 
        this.password = password; 
    }

    public List<Task> getTasks() 
    { 
        return tasks;
    }
    public void setTasks(List<Task> tasks) 
    { 
        this.tasks = tasks; 
    }

    public Role getRole() 
    {
        return role;
    }
    public void setRole(Role role) 
    {
        this.role = role;
    }
}
