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

  Scenario: Return database todo and find by id
    When I create a database task with title "WriteDB" description "Doc" tags:
      | work |
    Then the last returned database todo can be found by id
    And the last returned database todo's tags can be found by id

  Scenario: Database service counts
    Given the following database tasks exist:
      | title | description | tags | status    |
      | E     | e           | x    | completed |
      | F     | f           | y    | pending   |
    Then database service completed count is 1
    And database service pending count is 1

  Scenario: Database fuzzy search by text
    Given the following database tasks exist:
      | title      | description     | tags |
      | Buy milk   | Grocery store   | home |
      | Coffee mug | Kitchen utensil | home |
      | Run        | Morning jog     | fit  |
    When I search database tasks with query "cof"
    Then I should see 1 database search results

  Scenario: Database time search before now
    Given the following database tasks exist:
      | title     | description | tags |
      | Early A   | first       | a    |
      | Early B   | second      | b    |
    When I list database tasks before time "2026-12-31T23:59:59"
    Then I should see 2 database tasks
