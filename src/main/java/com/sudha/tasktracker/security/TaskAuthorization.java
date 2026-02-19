package com.sudha.tasktracker.security;

import com.sudha.tasktracker.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("taskAuth")
public class TaskAuthorization {

    private final TaskRepository taskRepository;

    public TaskAuthorization(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean isOwner(Long taskId, Authentication authentication) {
    if (authentication == null) return false;
    return taskRepository.existsByIdAndAssignedUser_UsernameIgnoreCase(
        taskId,authentication.getName());
}
    public boolean canDelete(Long taskId, Authentication authentication) {
        if (authentication == null) return false;
        return taskRepository.existsByIdAndAssignedUser_UsernameIgnoreCase(taskId, authentication.getName());
    }
}
