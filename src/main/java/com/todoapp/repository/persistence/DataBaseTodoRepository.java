package com.todoapp.repository.persistence;

import com.todoapp.domain.Tag;
import com.todoapp.domain.Todo;
import com.todoapp.entity.TodoDTO;
import com.todoapp.entity.TagDTO;
import com.todoapp.repository.TodoRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataBaseTodoRepository implements TodoRepository {
    private final DataSource dataSource;
    private final TodoDTORepository todoDTORepository;
    private final TagDTORepository tagDTORepository;

    public DataBaseTodoRepository(DataSource dataSource, TodoDTORepository todoDTORepository, TagDTORepository tagDTORepository) {
        this.dataSource = dataSource;
        this.todoDTORepository = todoDTORepository;
        this.tagDTORepository = tagDTORepository;
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
        dto = todoDTORepository.save(dto);
        todo.setId(dto.getId());
        try (Connection conn = dataSource.getConnection()) {
            upsertRelations(conn, todo.getId(), todo.getTags());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        Optional<TodoDTO> dtoOpt = todoDTORepository.findById(id);
        if (dtoOpt.isEmpty()) return Optional.empty();
        try (Connection conn = dataSource.getConnection()) {
            return Optional.of(mapTodo(conn, dtoOpt.get()));
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Todo> findByTitle(String title) {
        Optional<TodoDTO> dtoOpt = todoDTORepository.findByTitle(title);
        if (dtoOpt.isEmpty()) return Optional.empty();
        try (Connection conn = dataSource.getConnection()) {
            return Optional.of(mapTodo(conn, dtoOpt.get()));
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Todo> findAll() {
        List<TodoDTO> dtoList = todoDTORepository.findAll();
        try (Connection conn = dataSource.getConnection()) {
            return dtoList.stream().map(dto -> {
                try { return mapTodo(conn, dto); } catch (SQLException e) { throw new RuntimeException(e); }
            }).collect(Collectors.toList());
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM todo_tags WHERE todo_id=?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM todos WHERE id=?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Todo mapTodo(Connection conn, TodoDTO dto) throws SQLException {
        Todo t = new Todo();
        t.setId(dto.getId());
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setCompleted(dto.isCompleted());
        t.setCreatedAt(dto.getCreatedAt());
        t.setUpdatedAt(dto.getUpdatedAt());
        t.setTags(loadTagsForTodo(conn, t.getId()));
        return t;
    }

    private Set<Tag> loadTagsForTodo(Connection conn, Long todoId) throws SQLException {
        Set<Tag> set = new LinkedHashSet<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT t.id, t.name FROM todo_tags tt JOIN tags t ON tt.tag_id=t.id WHERE tt.todo_id=? ORDER BY t.id")) {
            ps.setLong(1, todoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tag tag = new Tag();
                    tag.setId(rs.getLong(1));
                    tag.setName(rs.getString(2));
                    set.add(tag);
                }
            }
        }
        return set;
    }

    private void upsertRelations(Connection conn, Long todoId, Set<Tag> tags) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM todo_tags WHERE todo_id=?")) {
            del.setLong(1, todoId);
            del.executeUpdate();
        }
        if (tags == null || tags.isEmpty()) return;
        try (PreparedStatement ins = conn.prepareStatement("INSERT INTO todo_tags(todo_id, tag_id) VALUES(?,?)")) {
            for (Tag tag : tags) {
                Long tagId = tag.getId();
                if (tagId == null) {
                    TagDTO dto = new TagDTO();
                    dto.setName(tag.getName());
                    dto = tagDTORepository.save(dto);
                    tagId = dto.getId();
                    tag.setId(tagId);
                }
                ins.setLong(1, todoId);
                ins.setLong(2, tagId);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }
}
