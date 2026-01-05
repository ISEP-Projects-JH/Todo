package com.todoapp.repository.persistence;

import com.todoapp.entity.TodoTagRelationDTO;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoTagRelationDTORepository extends CrudRepository<TodoTagRelationDTO, Long> {
    List<TodoTagRelationDTO> findByTodoId(Long todoId);

    List<TodoTagRelationDTO> findByTagId(Long tagId);

    @Modifying
    @Query("DELETE FROM todo_tags WHERE todo_id = :todoId")
    void deleteAllByTodoId(@Param("todoId") Long todoId);
}
