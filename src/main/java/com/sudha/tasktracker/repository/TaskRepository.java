package com.sudha.tasktracker.repository;

import com.sudha.tasktracker.model.Task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUser_UsernameIgnoreCase(String username);
}
