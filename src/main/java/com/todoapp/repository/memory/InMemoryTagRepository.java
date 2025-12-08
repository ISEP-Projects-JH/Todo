package com.todoapp.repository.memory;

import com.todoapp.domain.Tag;
import com.todoapp.repository.TagRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTagRepository implements TagRepository {
    private final Map<Long, Tag> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            tag.setId(seq.getAndIncrement());
        }
        store.put(tag.getId(), tag);
        return tag;
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return store.values().stream().filter(t -> Objects.equals(t.getName(), name)).findFirst();
    }

    @Override
    public List<Tag> findAll() {
        return new ArrayList<>(store.values());
    }
}
