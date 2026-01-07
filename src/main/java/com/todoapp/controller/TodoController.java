package com.todoapp.controller;

import com.todoapp.controller.dto.*;
import com.todoapp.domain.Todo;
import com.todoapp.service.DashboardService;
import com.todoapp.service.TodoService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;
    private final DashboardService dashboardService;

    public TodoController(TodoService todoService, DashboardService dashboardService) {
        this.todoService = todoService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(@RequestBody CreateTodoRequest request) {
        Todo todo = todoService.createTodo(request.getTitle(), request.getDescription(), request.getTags());
        return TodoResponse.from(todo);
    }

    @GetMapping("/todos")
    public List<TodoResponse> listTodos() {
        return todoService.listTodos().stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/todos/{title}")
    public TodoResponse getTodo(@PathVariable String title) {
        return TodoResponse.from(todoService.getByTitle(title));
    }

    @PutMapping("/todos/{title}")
    public TodoResponse updateTodo(@PathVariable String title, @RequestBody UpdateTodoRequest request) {
        String newTitle = request.getNewTitle() != null ? request.getNewTitle() : title;
        Todo todo = todoService.updateTodoByTitle(title, newTitle, request.getDescription(), request.getTags());
        return TodoResponse.from(todo);
    }

    @DeleteMapping("/todos/{title}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable String title) {
        todoService.deleteByTitle(title);
    }

    @DeleteMapping("/todos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Profile("test")
    public void clearAll() {
        todoService.clearAll();
    }

    @PutMapping("/todos/{title}/completed")
    public TodoResponse markCompleted(@PathVariable String title) {
        return TodoResponse.from(todoService.markCompletedByTitle(title));
    }

    @PutMapping("/todos/{title}/pending")
    public TodoResponse markPending(@PathVariable String title) {
        return TodoResponse.from(todoService.markPendingByTitle(title));
    }

    @PostMapping("/todos/{title}/tags")
    @Profile("test")
    public TodoResponse addTags(@PathVariable String title, @RequestBody TagOperationRequest request) {
        return TodoResponse.from(todoService.addTags(title, request.getTags()));
    }

    @DeleteMapping("/todos/{title}/tags")
    @Profile("test")
    public TodoResponse removeTags(@PathVariable String title, @RequestBody TagOperationRequest request) {
        return TodoResponse.from(todoService.removeTags(title, request.getTags()));
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {
        DashboardResponse response = new DashboardResponse();
        response.setCompletedCount(dashboardService.completedCount());
        response.setPendingCount(dashboardService.pendingCount());
        return response;
    }

    @GetMapping("/todos/search")
    public List<TodoResponse> searchTodos(@RequestParam("q") String query) {
        return todoService.searchTodos(query).stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/todos/before")
    public List<TodoResponse> listTodosBefore(@RequestParam("time") String timeStr, @RequestParam(value = "limit", defaultValue = "64") int limit) {
        try {
            LocalDateTime time = LocalDateTime.parse(timeStr);
            return todoService.listTodosBefore(time, limit).stream()
                    .map(TodoResponse::from)
                    .collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + timeStr);
        }
    }

    @GetMapping("/todos/search/tag")
    public List<TodoResponse> findByTag(@RequestParam("tag") String tag) {
        return todoService.findByTag(tag).stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }
}
