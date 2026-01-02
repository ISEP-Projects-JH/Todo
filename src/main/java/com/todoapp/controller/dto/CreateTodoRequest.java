package com.todoapp.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CreateTodoRequest {
    private String title;
    private String description;
    private Set<String> tags;
}
