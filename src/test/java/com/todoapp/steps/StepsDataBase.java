package com.todoapp.steps;

import com.todoapp.domain.Tag;
import com.todoapp.domain.Todo;
import com.todoapp.service.DashboardService;
import com.todoapp.service.TodoService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@CucumberContextConfiguration
@SpringBootTest
public class StepsDataBase {

    @Autowired
    private TodoService todoService;

    @Autowired
    private DashboardService dashboardService;

    private int lastListCount = 0;

    @Given("the database task list is empty")
    public void the_database_task_list_is_empty() {
        for (Todo t : new ArrayList<>(todoService.listTodos())) {
            todoService.deleteByTitle(t.getTitle());
        }
        lastListCount = 0;
    }

    @Given("the following database tasks exist:")
    public void the_following_database_tasks_exist(DataTable table) {
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

    @When("I create a database task with title {string} description {string} tags:")
    public void i_create_database_task(String title, String description, DataTable tagsTable) {
        todoService.createTodo(title, description, singleColumnToSet(tagsTable));
    }

    @Then("database task {string} exists")
    public void database_task_exists(String title) {
        Assert.assertNotNull(todoService.getByTitle(title));
    }

    @Then("database task {string} should not exist")
    public void database_task_should_not_exist(String title) {
        boolean exists = false;
        try {
            todoService.getByTitle(title);
            exists = true;
        } catch (IllegalArgumentException ignored) {
        }
        Assert.assertFalse(exists);
    }

    @Then("database task {string} has status {string}")
    public void database_task_has_status(String title, String status) {
        Todo t = todoService.getByTitle(title);
        boolean expected = "completed".equalsIgnoreCase(status);
        Assert.assertEquals(expected, t.isCompleted());
    }

    @Then("database task {string} has description {string}")
    public void database_task_has_description(String title, String description) {
        Todo t = todoService.getByTitle(title);
        Assert.assertEquals(description, t.getDescription());
    }

    @Then("database task {string} has tags:")
    public void database_task_has_tags(String title, DataTable tagsTable) {
        Todo t = todoService.getByTitle(title);
        Set<String> expected = singleColumnToSet(tagsTable);
        Set<String> actual = t.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
        Assert.assertTrue(actual.containsAll(expected));
    }

    @Then("database task {string} does not have tags:")
    public void database_task_does_not_have_tags(String title, DataTable tagsTable) {
        Todo t = todoService.getByTitle(title);
        Set<String> notExpected = singleColumnToSet(tagsTable);
        Set<String> actual = t.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
        for (String tag : notExpected) {
            Assert.assertFalse(actual.contains(tag));
        }
    }

    @When("I update database task {string} to title {string} description {string} tags:")
    public void i_update_database_task(String oldTitle, String newTitle, String description, DataTable tagsTable) {
        todoService.updateTodoByTitle(oldTitle, newTitle, description, singleColumnToSet(tagsTable));
    }

    @When("I mark database task {string} as completed")
    public void i_mark_database_task_completed(String title) {
        todoService.markCompletedByTitle(title);
    }

    @When("I mark database task {string} as pending")
    public void i_mark_database_task_pending(String title) {
        todoService.markPendingByTitle(title);
    }

    @When("I delete database task {string}")
    public void i_delete_database_task(String title) {
        todoService.deleteByTitle(title);
    }

    @When("I add tags to database task {string}:")
    public void i_add_tags_to_database_task(String title, DataTable tagsTable) {
        todoService.addTags(title, singleColumnToSet(tagsTable));
    }

    @When("I remove tags from database task {string}:")
    public void i_remove_tags_from_database_task(String title, DataTable tagsTable) {
        todoService.removeTags(title, singleColumnToSet(tagsTable));
    }

    @When("I list database tasks")
    public void i_list_database_tasks() {
        lastListCount = todoService.listTodos().size();
    }

    @Then("I should see {int} database tasks")
    public void i_should_see_database_tasks(Integer count) {
        Assert.assertEquals(count.intValue(), lastListCount);
    }

    @Then("database dashboard completed count is {int}")
    public void database_dashboard_completed_count(Integer count) {
        Assert.assertEquals(count.intValue(), dashboardService.completedCount());
    }

    @Then("database dashboard pending count is {int}")
    public void database_dashboard_pending_count(Integer count) {
        Assert.assertEquals(count.intValue(), dashboardService.pendingCount());
    }

    private Set<String> singleColumnToSet(DataTable table) {
        return new HashSet<>(table.asList());
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
