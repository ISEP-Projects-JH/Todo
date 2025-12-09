Feature: Todo core behavior

  Background:
    Given the task list is empty

  Scenario: Create a task
    When I create a task with title "Buy milk" description "Go to store" tags:
      | home |
    Then task "Buy milk" exists
    And task "Buy milk" has status "pending"
    And task "Buy milk" has tags:
      | home |
    And dashboard completed count is 0
    And dashboard pending count is 1

  Scenario: Update a task
    Given the following tasks exist:
      | title   | description | tags |
      | Buy milk | Go to store | home |
    When I update task "Buy milk" to title "Buy coffee" description "Starbucks" tags:
      | daily |
    Then task "Buy coffee" exists
    And task "Buy coffee" has description "Starbucks"
    And task "Buy coffee" has tags:
      | daily |
    And dashboard completed count is 0
    And dashboard pending count is 1

  Scenario: Mark as completed
    Given the following tasks exist:
      | title   | description | tags |
      | Buy milk | Go to store | home |
    When I mark task "Buy milk" as completed
    Then task "Buy milk" has status "completed"
    And dashboard completed count is 1
    And dashboard pending count is 0

  Scenario: Mark as pending
    Given the following tasks exist:
      | title   | description | tags |
      | Buy milk | Go to store | home |
    And I mark task "Buy milk" as completed
    When I mark task "Buy milk" as pending
    Then task "Buy milk" has status "pending"
    And dashboard completed count is 0
    And dashboard pending count is 1

  Scenario: Delete a task
    Given the following tasks exist:
      | title   | description | tags |
      | Buy milk | Go to store | home |
    When I delete task "Buy milk"
    Then task "Buy milk" should not exist
    And dashboard completed count is 0
    And dashboard pending count is 0

  Scenario: Add and remove tags
    Given the following tasks exist:
      | title | description | tags  |
      | Read  | Book       | study |
    When I add tags to task "Read":
      | hobby |
    And I remove tags from task "Read":
      | study |
    Then task "Read" has tags:
      | hobby |
    And task "Read" does not have tags:
      | study |

  Scenario: List and stats
    Given the following tasks exist:
      | title | description | tags | status    |
      | A     | a           | x    | completed |
      | B     | b           | y    | pending   |
    When I list tasks
    Then I should see 2 tasks
    And dashboard completed count is 1
    And dashboard pending count is 1

  Scenario: Return todo and find by id
    When I create a task with title "Write" description "Doc" tags:
      | work |
    Then the last returned todo can be found by id
    And the last returned todo's tags can be found by id

  Scenario: Service counts
    Given the following tasks exist:
      | title | description | tags | status    |
      | C     | c           | x    | completed |
      | D     | d           | y    | pending   |
    Then service completed count is 1
    And service pending count is 1
