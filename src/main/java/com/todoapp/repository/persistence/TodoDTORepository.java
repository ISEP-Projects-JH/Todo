package com.todoapp.repository.persistence;

import com.todoapp.entity.TodoDTO;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoDTORepository extends CrudRepository<TodoDTO, Long> {
    Optional<TodoDTO> findByTitle(String title);

    @NonNull
    List<TodoDTO> findAll();

    void deleteById(@NonNull Long id);
}
