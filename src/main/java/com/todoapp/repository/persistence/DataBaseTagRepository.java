package com.todoapp.repository.persistence;

import com.todoapp.domain.Tag;
import com.todoapp.repository.TagRepository;
import com.todoapp.entity.TagDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataBaseTagRepository implements TagRepository {
    private final TagDTORepository tagDTORepository;

    public DataBaseTagRepository(TagDTORepository tagDTORepository) {
        this.tagDTORepository = tagDTORepository;
    }

    @Override
    public Tag save(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto = tagDTORepository.save(dto);
        Tag result = new Tag();
        result.setId(dto.getId());
        result.setName(dto.getName());
        return result;
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return tagDTORepository.findById(id).map(dto -> {
            Tag t = new Tag();
            t.setId(dto.getId());
            t.setName(dto.getName());
            return t;
        });
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagDTORepository.findByName(name).map(dto -> {
            Tag t = new Tag();
            t.setId(dto.getId());
            t.setName(dto.getName());
            return t;
        });
    }

    @Override
    public List<Tag> findAll() {
        return tagDTORepository.findAll().stream().map(dto -> {
            Tag t = new Tag();
            t.setId(dto.getId());
            t.setName(dto.getName());
            return t;
        }).collect(Collectors.toList());
    }
}
