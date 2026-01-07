package com.todoapp.repository;

import com.todoapp.domain.Todo;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface TodoRepository {
    Todo save(Todo todo);

    Optional<Todo> findById(Long id);

    Optional<Todo> findByTitle(String title);

    List<Todo> findAll();

    void deleteById(Long id);

    List<Todo> searchByText(String query);

    List<Todo> findBefore(LocalDateTime before, int limit);

    List<Todo> findByTag(String tag);

    void deleteAll();
}
