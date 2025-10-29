package com.sudha.tasktracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.sudha.tasktracker.model.User;
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean completed = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
    public Long getId()
    { 
        return id; 
    }
    public void setId(Long id) 
    { 
        this.id = id; 
    }

    public String getTitle() 
    { 
        return title;
    }
    public void setTitle(String title) 
    { 
        this.title = title; 
    }

    public String getDescription() 
    { 
        return description; 
    }
    public void setDescription(String description) 
    { 
        this.description = description; 
    }

    public boolean isCompleted()
     { 
        return completed; 
    }
    public void setCompleted(boolean completed) 
    { 
        this.completed = completed; 
    }

    public LocalDateTime getCreatedAt() 
    { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) 
    { 
        this.createdAt = createdAt; 
    }
}
