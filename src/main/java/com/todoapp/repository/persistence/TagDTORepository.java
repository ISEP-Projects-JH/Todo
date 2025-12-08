package com.todoapp.repository.persistence;

import com.todoapp.entity.TagDTO;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagDTORepository extends CrudRepository<TagDTO, Long> {
    Optional<TagDTO> findByName(String name);

    @NonNull
    List<TagDTO> findAll();
}
