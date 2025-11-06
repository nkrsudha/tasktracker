package com.sudha.tasktracker.service;

import com.sudha.tasktracker.model.Task;
import com.sudha.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return repository.findById(id);
    }

    public Task createTask(Task task) {
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        return repository.findById(id)
                .map(task -> {

                    if (updatedTask.getTitle() != null) {
                        task.setTitle(updatedTask.getTitle());
                    }

                    if (updatedTask.getDescription() != null) {
                        task.setDescription(updatedTask.getDescription());

                    }
                    if (updatedTask.getCompleted() != null) {
                        task.setCompleted(updatedTask.getCompleted());
                    }
                    return repository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void deleteTask(Long id) {
        repository.deleteById(id);
    }
}
