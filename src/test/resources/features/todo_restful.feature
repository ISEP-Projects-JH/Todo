Feature: Todo restful behavior

  Background:
    Given the restful task list is empty

  Scenario: Create a task
    When I create a restful task with title "Buy milk" description "Go to store" tags:
      | home |
    Then restful task "Buy milk" exists
    And restful task "Buy milk" has status "pending"
    And restful task "Buy milk" has tags:
      | home |
    And restful dashboard completed count is 0
    And restful dashboard pending count is 1

  Scenario: Update a task
    Given the following restful tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    When I update restful task "Buy milk" to title "Buy coffee" description "Starbucks" tags:
      | daily |
    Then restful task "Buy coffee" exists
    And restful task "Buy coffee" has description "Starbucks"
    And restful task "Buy coffee" has tags:
      | daily |
    And restful dashboard completed count is 0
    And restful dashboard pending count is 1

  Scenario: Mark as completed
    Given the following restful tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    When I mark restful task "Buy milk" as completed
    Then restful task "Buy milk" has status "completed"
    And restful dashboard completed count is 1
    And restful dashboard pending count is 0

  Scenario: Mark as pending
    Given the following restful tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    And I mark restful task "Buy milk" as completed
    When I mark restful task "Buy milk" as pending
    Then restful task "Buy milk" has status "pending"
    And restful dashboard completed count is 0
    And restful dashboard pending count is 1

  Scenario: Delete a task
    Given the following restful tasks exist:
      | title    | description | tags |
      | Buy milk | Go to store | home |
    When I delete restful task "Buy milk"
    Then restful task "Buy milk" should not exist
    And restful dashboard completed count is 0
    And restful dashboard pending count is 0

  Scenario: Add and remove tags
    Given the following restful tasks exist:
      | title | description | tags  |
      | Read  | Book        | study |
    When I add tags to restful task "Read":
      | hobby |
    And I remove tags from restful task "Read":
      | study |
    Then restful task "Read" has tags:
      | hobby |
    And restful task "Read" does not have tags:
      | study |

  Scenario: List and stats
    Given the following restful tasks exist:
      | title | description | tags | status    |
      | A     | a           | x    | completed |
      | B     | b           | y    | pending   |
    When I list restful tasks
    Then I should see 2 restful tasks
    And restful dashboard completed count is 1
    And restful dashboard pending count is 1

  Scenario: Return restful todo and find by id
    When I create a restful task with title "WriteREST" description "Doc" tags:
      | work |
    Then the last returned restful todo can be found by id
    And the last returned restful todo's tags can be found by id

  Scenario: Restful service counts
    Given the following restful tasks exist:
      | title | description | tags | status    |
      | E     | e           | x    | completed |
      | F     | f           | y    | pending   |
    Then restful service completed count is 1
    And restful service pending count is 1

  Scenario: Restful fuzzy search by text
    Given the following restful tasks exist:
      | title      | description     | tags |
      | Buy milk   | Grocery store   | home |
      | Coffee mug | Kitchen utensil | home |
      | Run        | Morning jog     | fit  |
    When I search restful tasks with query "cof"
    Then I should see 1 restful search results

  Scenario: Restful time search before now
    Given the following restful tasks exist:
      | title     | description | tags |
      | Early A   | first       | a    |
      | Early B   | second      | b    |
    When I list restful tasks before time "2026-12-31T23:59:59"
    Then I should see 2 restful tasks
