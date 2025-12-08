package com.todoapp.repository.persistence;

import com.todoapp.domain.Tag;
import com.todoapp.domain.Todo;
import com.todoapp.entity.TodoDTO;
import com.todoapp.entity.TagDTO;
import com.todoapp.entity.TodoTagRelationDTO;
import com.todoapp.repository.TodoRepository;

import java.util.*;
import java.util.stream.Collectors;

public class DataBaseTodoRepository implements TodoRepository {
    private final TodoDTORepository todoRepo;
    private final TagDTORepository tagRepo;
    private final TodoTagRelationDTORepository relationRepo;

    public DataBaseTodoRepository(
            TodoDTORepository todoRepo,
            TagDTORepository tagRepo,
            TodoTagRelationDTORepository relationRepo
    ) {
        this.todoRepo = todoRepo;
        this.tagRepo = tagRepo;
        this.relationRepo = relationRepo;
    }

    @Override
    public Todo save(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setCompleted(todo.isCompleted());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());

        dto = todoRepo.save(dto);
        todo.setId(dto.getId());

        upsertRelations(todo.getId(), todo.getTags());

        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoRepo.findById(id)
                .map(this::mapTodo);
    }

    @Override
    public Optional<Todo> findByTitle(String title) {
        return todoRepo.findByTitle(title)
                .map(this::mapTodo);
    }

    @Override
    public List<Todo> findAll() {
        return todoRepo.findAll()
                .stream()
                .map(this::mapTodo)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        relationRepo.deleteAllByTodoId(id);
        todoRepo.deleteById(id);
    }

    private Todo mapTodo(TodoDTO dto) {
        Todo t = new Todo();
        t.setId(dto.getId());
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setCompleted(dto.isCompleted());
        t.setCreatedAt(dto.getCreatedAt());
        t.setUpdatedAt(dto.getUpdatedAt());

        List<TodoTagRelationDTO> relations = relationRepo.findByTodoId(dto.getId());
        Set<Tag> tags = relations.stream()
                .map(rel -> tagRepo.findById(rel.getTagId()))
                .filter(Optional::isPresent)
                .map(opt -> {
                    Tag tag = new Tag();
                    tag.setId(opt.get().getId());
                    tag.setName(opt.get().getName());
                    return tag;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        t.setTags(tags);
        return t;
    }

    private void upsertRelations(Long todoId, Set<Tag> tags) {
        relationRepo.deleteAllByTodoId(todoId);
        if (tags == null || tags.isEmpty()) return;

        for (Tag tag : tags) {
            Long tagId = tag.getId();

            if (tagId == null) {
                TagDTO dto = new TagDTO();
                dto.setName(tag.getName());
                dto = tagRepo.save(dto);
                tagId = dto.getId();
                tag.setId(tagId);
            }

            TodoTagRelationDTO relation = new TodoTagRelationDTO();
            relation.setTodoId(todoId);
            relation.setTagId(tagId);
            relationRepo.save(relation);
        }
    }
}
