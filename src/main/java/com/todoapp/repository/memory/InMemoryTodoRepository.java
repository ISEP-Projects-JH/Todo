package com.todoapp.repository.memory;

import com.todoapp.domain.Todo;
import com.todoapp.repository.TodoRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class InMemoryTodoRepository implements TodoRepository {
    private final Map<Long, Todo> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(seq.getAndIncrement());
        }
        store.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Todo> findByTitle(String title) {
        return store.values().stream().filter(t -> Objects.equals(t.getTitle(), title)).findFirst();
    }

    @Override
    public List<Todo> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public List<Todo> searchByText(String query) {
        String q = query == null ? "" : query.toLowerCase();
        return store.values().stream()
                .filter(t -> {
                    String title = t.getTitle() == null ? "" : t.getTitle().toLowerCase();
                    String desc = t.getDescription() == null ? "" : t.getDescription().toLowerCase();
                    return title.contains(q) || desc.contains(q);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Todo> findBefore(LocalDateTime before, int limit) {
        LocalDateTime b = before == null ? LocalDateTime.now() : before;
        return store.values().stream()
                .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isBefore(b))
                .sorted(Comparator.comparing(Todo::getCreatedAt).reversed())
                .limit(limit)
                .sorted(Comparator.comparing(Todo::getCreatedAt))
                .collect(Collectors.toList());
    }
}
