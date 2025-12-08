package com.todoapp.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("tags")
public class TagDTO {
    @Id
    private Long id;
    private String name;
}
