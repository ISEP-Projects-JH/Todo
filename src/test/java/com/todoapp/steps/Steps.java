package com.todoapp.steps;

import com.todoapp.service.DashboardService;
import com.todoapp.service.TodoService;
import com.todoapp.repository.memory.InMemoryTagRepository;
import com.todoapp.repository.memory.InMemoryTodoRepository;
import com.todoapp.domain.Todo;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class Steps {
    private final InMemoryTodoRepository todoRepository = new InMemoryTodoRepository();
    private final InMemoryTagRepository tagRepository = new InMemoryTagRepository();
    private final TodoService todoService = new TodoService(todoRepository, tagRepository);
    private final DashboardService dashboardService = new DashboardService(todoRepository);
    private int lastListCount = 0;

    @Given("the task list is empty")
    public void the_task_list_is_empty() {
        for (Todo t : new ArrayList<>(todoRepository.findAll())) {
            todoRepository.deleteById(t.getId());
        }
        lastListCount = 0;
    }

    @Given("the following tasks exist:")
    public void the_following_tasks_exist(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String title = row.getOrDefault("title", "");
            String description = row.getOrDefault("description", "");
            String tagsStr = row.getOrDefault("tags", "");
            String status = row.getOrDefault("status", "");
            Set<String> tagNames = parseTags(tagsStr);
            todoService.createTodo(title, description, tagNames);
            if ("completed".equalsIgnoreCase(status)) {
                todoService.markCompletedByTitle(title);
            } else if ("pending".equalsIgnoreCase(status) && todoService.getByTitle(title).isCompleted()) {
                todoService.markPendingByTitle(title);
            }
        }
    }

    @When("I create a task with title {string} description {string} tags:")
    public void i_create_a_task_with_title_description_tags(String title, String description, DataTable tagsTable) {
        Set<String> tags = singleColumnToSet(tagsTable);
        todoService.createTodo(title, description, tags);
    }

    @Then("task {string} exists")
    public void task_exists(String title) {
        Assert.assertNotNull(todoService.getByTitle(title));
    }

    @Then("task {string} should not exist")
    public void task_should_not_exist(String title) {
        boolean exists = false;
        try { todoService.getByTitle(title); exists = true; } catch (IllegalArgumentException ignored) {}
        Assert.assertFalse(exists);
    }

    @Then("task {string} has status {string}")
    public void task_has_status(String title, String status) {
        Todo t = todoService.getByTitle(title);
        boolean expected = "completed".equalsIgnoreCase(status);
        Assert.assertEquals(expected, t.isCompleted());
    }

    @Then("task {string} has description {string}")
    public void task_has_description(String title, String description) {
        Todo t = todoService.getByTitle(title);
        Assert.assertEquals(description, t.getDescription());
    }

    @Then("task {string} has tags:")
    public void task_has_tags(String title, DataTable tagsTable) {
        Todo t = todoService.getByTitle(title);
        Set<String> expected = singleColumnToSet(tagsTable);
        Set<String> actual = t.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
        Assert.assertTrue(actual.containsAll(expected));
    }

    @Then("task {string} does not have tags:")
    public void task_does_not_have_tags(String title, DataTable tagsTable) {
        Todo t = todoService.getByTitle(title);
        Set<String> notExpected = singleColumnToSet(tagsTable);
        Set<String> actual = t.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
        for (String tag : notExpected) {
            Assert.assertFalse(actual.contains(tag));
        }
    }

    @When("I update task {string} to title {string} description {string} tags:")
    public void i_update_task_to(String oldTitle, String newTitle, String description, DataTable tagsTable) {
        todoService.updateTodoByTitle(oldTitle, newTitle, description, singleColumnToSet(tagsTable));
    }

    @When("I mark task {string} as completed")
    public void i_mark_task_as_completed(String title) {
        todoService.markCompletedByTitle(title);
    }

    @When("I mark task {string} as pending")
    public void i_mark_task_as_pending(String title) {
        todoService.markPendingByTitle(title);
    }

    @When("I delete task {string}")
    public void i_delete_task(String title) {
        todoService.deleteByTitle(title);
    }

    @When("I add tags to task {string}:")
    public void i_add_tags_to_task(String title, DataTable tagsTable) {
        todoService.addTags(title, singleColumnToSet(tagsTable));
    }

    @When("I remove tags from task {string}:")
    public void i_remove_tags_from_task(String title, DataTable tagsTable) {
        todoService.removeTags(title, singleColumnToSet(tagsTable));
    }

    @When("I list tasks")
    public void i_list_tasks() {
        lastListCount = todoService.listTodos().size();
    }

    @Then("I should see {int} tasks")
    public void i_should_see_tasks(Integer count) {
        Assert.assertEquals(count.intValue(), lastListCount);
    }

    @Then("dashboard completed count is {int}")
    public void dashboard_completed_count_is(Integer count) {
        Assert.assertEquals(count.intValue(), dashboardService.completedCount());
    }

    @Then("dashboard pending count is {int}")
    public void dashboard_pending_count_is(Integer count) {
        Assert.assertEquals(count.intValue(), dashboardService.pendingCount());
    }

    private Set<String> singleColumnToSet(DataTable table) {
        List<String> list = table.asList();
        return new HashSet<>(list);
    }

    private Set<String> parseTags(String tagsStr) {
        String[] parts = tagsStr.split("[,\\s]+");
        Set<String> set = new HashSet<>();
        for (String p : parts) {
            if (!p.trim().isEmpty()) set.add(p.trim());
        }
        return set;
    }
}
