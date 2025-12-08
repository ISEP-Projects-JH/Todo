package com.todoapp.repository.persistence;

import com.todoapp.entity.TodoTagRelationDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoTagRelationDTORepository extends CrudRepository<TodoTagRelationDTO, Long> {
    List<TodoTagRelationDTO> findByTodoId(Long todoId);
    void deleteByTodoId(Long todoId);
}
