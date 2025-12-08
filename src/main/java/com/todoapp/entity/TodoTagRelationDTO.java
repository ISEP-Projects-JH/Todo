package com.todoapp.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("todo_tags")
public class TodoTagRelationDTO {
    @Id
    private Long id;
    private Long todoId;
    private Long tagId;
}
