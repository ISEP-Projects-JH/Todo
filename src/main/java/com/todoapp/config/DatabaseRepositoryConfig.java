package com.todoapp.config;

import com.todoapp.repository.TagRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.repository.persistence.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(
        name = "todo.repository.type",
        havingValue = "database",
        matchIfMissing = true
)
@Import(DataSourceAutoConfiguration.class)
public class DatabaseRepositoryConfig {

    @Bean
    public TodoRepository todoRepository(
            TodoDTORepository todoDTORepository,
            TagDTORepository tagDTORepository,
            TodoTagRelationDTORepository todoTagRelationDTORepository
    ) {
        return new DataBaseTodoRepository(
                todoDTORepository,
                tagDTORepository,
                todoTagRelationDTORepository
        );
    }

    @Bean
    public TagRepository tagRepository(TagDTORepository tagDTORepository) {
        return new DataBaseTagRepository(tagDTORepository);
    }
}
