package com.todoapp.config;

import com.todoapp.repository.TagRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.repository.persistence.DataBaseTagRepository;
import com.todoapp.repository.persistence.DataBaseTodoRepository;
import com.todoapp.repository.persistence.TagDTORepository;
import com.todoapp.repository.persistence.TodoDTORepository;
import com.todoapp.service.DashboardService;
import com.todoapp.service.TodoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean
    public TodoRepository todoRepository(DataSource dataSource, TodoDTORepository todoDTORepository, TagDTORepository tagDTORepository) {
        return new DataBaseTodoRepository(dataSource, todoDTORepository, tagDTORepository);
    }

    @Bean
    public TagRepository tagRepository(TagDTORepository tagDTORepository) {
        return new DataBaseTagRepository(tagDTORepository);
    }

    @Bean
    public TodoService todoService(TodoRepository todoRepository, TagRepository tagRepository) {
        return new TodoService(todoRepository, tagRepository);
    }

    @Bean
    public DashboardService dashboardService(TodoRepository todoRepository) {
        return new DashboardService(todoRepository);
    }
}
