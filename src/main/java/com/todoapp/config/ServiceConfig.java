package com.todoapp.config;

import com.todoapp.repository.TagRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.DashboardService;
import com.todoapp.service.TodoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public TodoService todoService(
            TodoRepository todoRepository,
            TagRepository tagRepository
    ) {
        return new TodoService(todoRepository, tagRepository);
    }

    @Bean
    public DashboardService dashboardService(
            TodoRepository todoRepository
    ) {
        return new DashboardService(todoRepository);
    }
}

