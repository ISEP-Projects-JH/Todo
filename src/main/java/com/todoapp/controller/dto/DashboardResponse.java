package com.todoapp.controller.dto;

import lombok.Data;

@Data
public class DashboardResponse {
    private int completedCount;
    private int pendingCount;
}
