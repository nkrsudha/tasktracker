package com.sudha.tasktracker.service;

import com.sudha.tasktracker.dto.TaskRequest;
import com.sudha.tasktracker.model.Task;
import com.sudha.tasktracker.model.TaskStatus;
import com.sudha.tasktracker.model.User;
import com.sudha.tasktracker.repository.TaskRepository;
import com.sudha.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldReturnAllTasks() {
        Task task1 = new Task();
        task1.setId(1L);

        Task task2 = new Task();
        task2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldReturnTasksByUsername() {
        Task task = new Task();
        task.setId(1L);

        when(repository.findByAssignedUser_UsernameIgnoreCase("testuser"))
                .thenReturn(List.of(task));

        List<Task> tasks = taskService.getTasksByUsername("testuser");

        assertEquals(1, tasks.size());
        verify(repository, times(1))
                .findByAssignedUser_UsernameIgnoreCase("testuser");
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task();
        task.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getTaskById(99L);

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void shouldCreateTaskSuccessfullyWithUser() {
        User user = new User();
        user.setId(1L);

        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setUserId(1L);
        request.setStatus(TaskStatus.IN_PROGRESS);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = taskService.createTask(request);

        assertNotNull(savedTask);
        assertEquals("Test Task", savedTask.getTitle());
        assertEquals("Test Description", savedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, savedTask.getStatus());
        assertEquals(user, savedTask.getAssignedUser());

        verify(userRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldCreateTaskWithDefaultStatusWhenStatusIsNull() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task");
        request.setDescription("Description");
        request.setStatus(null);

        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = taskService.createTask(request);

        assertEquals(TaskStatus.TO_DO, savedTask.getStatus());
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingTaskWithInvalidUser() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task");
        request.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request)
        );

        assertEquals("User not found", exception.getMessage());
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.TO_DO);

        Task updatedTask = new Task();
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");
        updatedTask.setStatus(TaskStatus.COMPLETED);

        when(repository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1L, updatedTask);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(existingTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingTask() {
        Task updatedTask = new Task();
        updatedTask.setTitle("New Title");

        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.updateTask(99L, updatedTask)
        );

        assertEquals("Task not found", exception.getMessage());
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskById() {
        taskService.deleteTask(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void shouldAssignUserToTask() {
        Task task = new Task();
        task.setId(1L);

        User user = new User();
        user.setId(2L);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.assignUser(1L, 2L);

        assertEquals(user, result.getAssignedUser());
        verify(repository, times(1)).save(task);
    }

    @Test
    void shouldUnassignUserWhenUserIdIsNull() {
        User user = new User();
        user.setId(2L);

        Task task = new Task();
        task.setId(1L);
        task.setAssignedUser(user);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.assignUser(1L, null);

        assertNull(result.getAssignedUser());
        verify(repository, times(1)).save(task);
    }

    @Test
    void shouldThrowExceptionWhenAssigningUserToMissingTask() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.assignUser(99L, 1L)
        );

        assertEquals("Task not found", exception.getMessage());
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenAssigningMissingUser() {
        Task task = new Task();
        task.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.assignUser(1L, 99L)
        );

        assertEquals("User not found", exception.getMessage());
        verify(repository, never()).save(any(Task.class));
    }
}

