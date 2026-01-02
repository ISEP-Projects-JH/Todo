package com.todoapp.controller.dto;

import lombok.Data;
import java.util.Set;

@Data
public class TagOperationRequest {
    private Set<String> tags;
}
