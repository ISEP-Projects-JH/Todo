package com.todoapp.controller.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UpdateTodoRequest {
    private String newTitle;
    private String description;
    private Set<String> tags;
}
