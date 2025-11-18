package com.sudha.tasktracker.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users") // rename table to avoid conflicts with SQL keyword "user"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    // One user can have many tasks
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"user"}) // to prevent recursion during serialization
    private List<Task> tasks;

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
}
