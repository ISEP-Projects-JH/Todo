package com.todoapp.repository;

import com.todoapp.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);

    Optional<Todo> findById(Long id);

    Optional<Todo> findByTitle(String title);

    List<Todo> findAll();

    void deleteById(Long id);
}
