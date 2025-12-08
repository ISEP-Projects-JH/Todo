package com.todoapp.service;

import com.todoapp.domain.Tag;
import com.todoapp.domain.Todo;
import com.todoapp.repository.TagRepository;
import com.todoapp.repository.TodoRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TodoService {
    private final TodoRepository todoRepository;
    private final TagRepository tagRepository;

    public TodoService(TodoRepository todoRepository, TagRepository tagRepository) {
        this.todoRepository = todoRepository;
        this.tagRepository = tagRepository;
    }

    public Todo createTodo(String title, String description, Set<String> tagNames) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(todo.getCreatedAt());
        todo.setTags(resolveTags(tagNames));
        return todoRepository.save(todo);
    }

    public Todo updateTodoByTitle(String oldTitle, String newTitle, String description, Set<String> tagNames) {
        Todo todo = getByTitle(oldTitle);
        todo.setTitle(newTitle);
        todo.setDescription(description);
        todo.setTags(resolveTags(tagNames));
        todo.setUpdatedAt(LocalDateTime.now());
        return todoRepository.save(todo);
    }

    public void deleteByTitle(String title) {
        Todo todo = getByTitle(title);
        todoRepository.deleteById(todo.getId());
    }

    public Todo markCompletedByTitle(String title) {
        Todo todo = getByTitle(title);
        todo.markCompleted();
        return todoRepository.save(todo);
    }

    public Todo markPendingByTitle(String title) {
        Todo todo = getByTitle(title);
        todo.markPending();
        return todoRepository.save(todo);
    }

    public List<Todo> listTodos() {
        return todoRepository.findAll();
    }

    public Todo addTags(String title, Set<String> tagNames) {
        Todo todo = getByTitle(title);
        todo.getTags().addAll(resolveTags(tagNames));
        todo.setUpdatedAt(LocalDateTime.now());
        return todoRepository.save(todo);
    }

    public Todo removeTags(String title, Set<String> tagNames) {
        Todo todo = getByTitle(title);
        Set<Tag> toRemove = resolveTags(tagNames);
        todo.getTags().removeAll(toRemove);
        todo.setUpdatedAt(LocalDateTime.now());
        return todoRepository.save(todo);
    }

    public int countCompleted() {
        return (int) todoRepository.findAll().stream().filter(Todo::isCompleted).count();
    }

    public int countPending() {
        return (int) todoRepository.findAll().stream().filter(t -> !t.isCompleted()).count();
    }

    public Todo getByTitle(String title) {
        Optional<Todo> opt = todoRepository.findByTitle(title);
        if (opt.isEmpty()) throw new IllegalArgumentException("Todo not found: " + title);
        return opt.get();
    }

    private Set<Tag> resolveTags(Set<String> names) {
        Set<Tag> set = new HashSet<>();
        if (names == null) return set;
        for (String n : names) {
            if (n == null || n.trim().isEmpty()) continue;
            String name = n.trim();
            Tag tag = tagRepository.findByName(name).orElseGet(() -> {
                Tag t = new Tag();
                t.setName(name);
                return tagRepository.save(t);
            });
            set.add(tag);
        }
        return set;
    }
}
