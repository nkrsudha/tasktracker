package com.sudha.tasktracker.controller;

import com.sudha.tasktracker.model.Task;
import com.sudha.tasktracker.dto.TaskRequest;
import com.sudha.tasktracker.service.TaskService;
import com.sudha.tasktracker.model.TaskStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    @PutMapping("/{id}/assign")
public Task assignUserToTask(@PathVariable Long id, @RequestBody Map<String, Long> body) {

    Long userId = body.get("userId");

    return service.assignUser(id, userId);
}

@GetMapping("/by-user/{username}")
public List<TaskPopupDto> getTasksByUsername(@PathVariable String username) {
    return service.getTasksByUsername(username)
            .stream()
            .map(t -> new TaskPopupDto(t.getTitle(), String.valueOf(t.getStatus())))
            .toList();
}
public record TaskPopupDto(String title, String status) {}
}
