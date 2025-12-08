package com.todoapp.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Todo {

    @EqualsAndHashCode.Include
    private Long id;

    private String title;
    private String description;
    private boolean completed;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<Tag> tags = new HashSet<>();

    public void markCompleted() {
        this.completed = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markPending() {
        this.completed = false;
        this.updatedAt = LocalDateTime.now();
    }

    public Todo() {
        this.createdAt = LocalDateTime.now();
    }
}
