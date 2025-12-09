package com.todoapp.service;

import com.todoapp.domain.Todo;
import com.todoapp.repository.TodoRepository;

public class DashboardService {
    private final TodoRepository todoRepository;

    public DashboardService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public int completedCount() {
        return (int) todoRepository.findAll().stream().filter(Todo::isCompleted).count();
    }

    public int pendingCount() {
        return (int) todoRepository.findAll().stream().filter(t -> !t.isCompleted()).count();
    }
}
