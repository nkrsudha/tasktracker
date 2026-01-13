package com.sudha.tasktracker.service;

import com.sudha.tasktracker.model.TaskStatus;
import com.sudha.tasktracker.model.Task;
import com.sudha.tasktracker.model.User;
import com.sudha.tasktracker.dto.TaskRequest;
import com.sudha.tasktracker.repository.TaskRepository;
import com.sudha.tasktracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Task> getAllTasks() 
    {
        return repository.findAll();
    }

    public Optional<Task> getTaskById(Long id) 
    {
        return repository.findById(id);
    }

    // NEW: Create task using DTO
    public Task createTask(TaskRequest request) {

        User user = null;

        if (request.getUserId() != null)
         user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
      
        task.setAssignedUser(user);

        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TO_DO);
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        return repository.findById(id)
                .map(task -> {

                    if (updatedTask.getTitle() != null) 
                    {
                        task.setTitle(updatedTask.getTitle());
                    }

                    if (updatedTask.getDescription() != null) 
                    {
                        task.setDescription(updatedTask.getDescription());
                    }
                    if (updatedTask.getStatus() != null) 
                    {
                        task.setStatus(updatedTask.getStatus());
                    }

                    return repository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void deleteTask(Long id) {
        repository.deleteById(id);
    }
    public Task assignUser(Long taskId, Long userId) {

    Task task = repository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

    if (userId == null) {
        task.setAssignedUser(null);
    } else {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setAssignedUser(user);
    }

    return repository.save(task);
}

}

 
