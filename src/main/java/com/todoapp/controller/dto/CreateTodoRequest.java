package com.todoapp.controller.dto;

import lombok.Data;
import java.util.Set;

@Data
public class CreateTodoRequest {
    private String title;
    private String description;
    private Set<String> tags;
}
