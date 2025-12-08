package com.todoapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("todo_tags")
public class TodoTagRelationDTO {
    @Id
    private Long id;
    private Long todoId;
    private Long tagId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTodoId() { return todoId; }
    public void setTodoId(Long todoId) { this.todoId = todoId; }

    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }
}
