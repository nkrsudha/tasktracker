package com.sudha.tasktracker.controller;

import com.sudha.tasktracker.model.Task;
import com.sudha.tasktracker.dto.TaskRequest;
import com.sudha.tasktracker.service.TaskService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // allows frontend apps to connect
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return service.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        return service.createTask(request);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return service.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
    }
}
