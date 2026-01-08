package com.todoapp.config;

import com.todoapp.repository.TagRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.repository.memory.InMemoryTagRepository;
import com.todoapp.repository.memory.InMemoryTodoRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "todo.repository.type",
        havingValue = "memory"
)
public class InMemoryRepositoryConfig {

    @Bean
    public TodoRepository todoRepository() {
        return new InMemoryTodoRepository();
    }

    @Bean
    public TagRepository tagRepository() {
        return new InMemoryTagRepository();
    }
}
