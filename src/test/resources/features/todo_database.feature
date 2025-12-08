Feature: Todo database behavior

  Background:
    Given the database task list is empty

  Scenario: Create a task
    When I create a database task with title "Buy milk" description "Go to store" tags:
      | home |
    Then database task "Buy milk" exists
    And database task "Buy milk" has status "pending"
    And database task "Buy milk" has tags:
      | home |
    And database dashboard completed count is 0
    And database dashboard pending count is 1

  Scenario: Update a task
    Given the following database tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    When I update database task "Buy milk" to title "Buy coffee" description "Starbucks" tags:
      | daily |
    Then database task "Buy coffee" exists
    And database task "Buy coffee" has description "Starbucks"
    And database task "Buy coffee" has tags:
      | daily |
    And database dashboard completed count is 0
    And database dashboard pending count is 1

  Scenario: Mark as completed
    Given the following database tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    When I mark database task "Buy milk" as completed
    Then database task "Buy milk" has status "completed"
    And database dashboard completed count is 1
    And database dashboard pending count is 0

  Scenario: Mark as pending
    Given the following database tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    And I mark database task "Buy milk" as completed
    When I mark database task "Buy milk" as pending
    Then database task "Buy milk" has status "pending"
    And database dashboard completed count is 0
    And database dashboard pending count is 1

  Scenario: Delete a task
    Given the following database tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    When I delete database task "Buy milk"
    Then database task "Buy milk" should not exist
    And database dashboard completed count is 0
    And database dashboard pending count is 0

  Scenario: Add and remove tags
    Given the following database tasks exist:
      | title | description | tags  |
      | Read  | Book        | study |
    When I add tags to database task "Read":
      | hobby |
    And I remove tags from database task "Read":
      | study |
    Then database task "Read" has tags:
      | hobby |
    And database task "Read" does not have tags:
      | study |

  Scenario: List and stats
    Given the following database tasks exist:
      | title | description | tags | status    |
      | A     | a           | x    | completed |
      | B     | b           | y    | pending   |
    When I list database tasks
    Then I should see 2 database tasks
    And database dashboard completed count is 1
    And database dashboard pending count is 1
