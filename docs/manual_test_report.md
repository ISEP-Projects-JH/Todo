# Test Report

**Todo Application**

---

## 1. Test Objective

The objective of this testing activity is to verify the main functionalities of the Todo application.
The testing focuses on ensuring that users can navigate between pages, manage tasks correctly, view accurate dashboard statistics, use search and filter features, and that the system behaves reasonably in invalid or edge-case scenarios.

---

## 2. Test Scope

The scope of this testing includes the following functionalities:

* Page loading and navigation between **Dashboard** and **Todos** pages
* Creating new tasks with different input conditions
* Editing, completing, and deleting tasks
* Updating and displaying Dashboard statistics
* Searching and filtering tasks by text, date, and tag
* Data persistence after page refresh
* Basic validation and error handling (e.g. duplicate task titles)

The testing does not include performance testing, security testing, or cross-browser compatibility testing.

---

## 3. Test Environment

* **Application Type**: Web application
* **Browser**: Google Chrome
* **Operating System**: Desktop environment
* **Test Data**: Manually created tasks during testing

---

## 4. Test Execution Summary

A total of **30 test cases** were designed and executed based on the functional requirements of the Todo application.

| Result | Number of Test Cases |
|--------|----------------------|
| Passed | 29                   |
| Failed | 1                    |
| Total  | 30                   |

Most of the core functionalities worked as expected during testing.

---

## 5. Test Results Overview

The following functionalities were verified successfully:

* Dashboard and Todos pages load correctly
* Navigation between pages works properly
* Tasks can be created, edited, completed, and deleted
* Dashboard statistics update correctly according to task status
* Search and filter functions return correct results
* Task data and statistics persist after page refresh

One issue was identified related to user feedback when invalid input is provided.

---

## 6. Defects Found

During testing, **one defect** was identified:

* When attempting to add a task with a duplicate title, the system correctly prevents the task from being added.
* However, no error message or user feedback is displayed to inform the user why the operation failed.

This issue does not affect system stability or data integrity, but it negatively impacts usability and user experience.

**Related Test Case**: TC15

---

## 7. Risk and Impact Analysis

* **Functional Impact**: Low
* **Usability Impact**: Medium

Users may be confused when an operation fails without any explanation, which could lead to repeated attempts or misunderstanding of system behavior.

---

## 8. Conclusion

Overall, the Todo application meets its main functional requirements and performs correctly in most tested scenarios.
All essential features are operational, and the system behaves consistently under normal usage.

The identified defect should be addressed to improve user experience by providing clear feedback when a duplicate task title is submitted.

---

## 9. Recommendation

It is recommended to display a clear error message or notification when a user attempts to create a task with a duplicate title.
This improvement would enhance usability and make the system behavior more transparent to users.

---

## 10. Detailed Test Execution Results (Appendix)

This appendix provides the detailed execution records of the manual test cases.
The table below was used as a working document during the test execution phase
to record actual results and pass/fail status for each test case.

To avoid redundancy, the main body of this test report presents a summarized view
of the execution results and focuses on the analysis of failed test cases only.
Detailed execution results are therefore provided here for reference purposes.

| Test Case ID | Expected Result                                               | Actual Result                          | Status |
|--------------|---------------------------------------------------------------|----------------------------------------|--------|
| TC01         | Dashboard page loads correctly without errors or blank screen | Page loads normally                    | Pass   |
| TC02         | Todos page loads correctly and all UI elements are displayed  | Page loads normally                    | Pass   |
| TC03         | System navigates to the Todos page successfully               | Navigation works correctly             | Pass   |
| TC04         | System navigates to the Dashboard page successfully           | Navigation works correctly             | Pass   |
| TC05         | Current page tab is highlighted correctly                     | Highlight works correctly              | Pass   |
| TC06         | No crash, white screen, or UI error occurs                    | Page behaves normally                  | Pass   |
| TC07         | Pending = 0 and Completed = 0                                 | Values displayed correctly             | Pass   |
| TC08         | Pending count increases by 1                                  | Count updated correctly                | Pass   |
| TC09         | Completed count increases by 1                                | Count updated correctly                | Pass   |
| TC10         | Statistics remain correct after refresh                       | Data remains correct                   | Pass   |
| TC11         | Task is added successfully and displayed in the list          | Task added successfully                | Pass   |
| TC12         | System prevents submission                                    | Task not added                         | Pass   |
| TC13         | Task is added successfully                                    | Task added successfully                | Pass   |
| TC14         | Task is added successfully                                    | Task added successfully                | Pass   |
| TC15         | System prevents adding and displays an error message          | Task not added, no error message shown | Fail   |
| TC16         | Task enters edit mode                                         | Edit mode displayed correctly          | Pass   |
| TC17         | Task information is updated successfully                      | Task updated successfully              | Pass   |
| TC18         | Changes are discarded and data remains unchanged              | Data unchanged                         | Pass   |
| TC19         | Task status changes to completed                              | Status updated correctly               | Pass   |
| TC20         | Completed task is visually distinguished                      | Style displayed correctly              | Pass   |
| TC21         | Task status returns to pending                                | Status updated correctly               | Pass   |
| TC22         | Task is removed from the task list                            | Task removed successfully              | Pass   |
| TC23         | Statistics are updated correctly                              | Statistics correct                     | Pass   |
| TC24         | Only matching tasks are displayed                             | Results displayed correctly            | Pass   |
| TC25         | “No result” message is displayed                              | Message displayed correctly            | Pass   |
| TC26         | Tasks from the selected date are displayed                    | Tasks displayed correctly              | Pass   |
| TC27         | Tasks with matching tag are displayed                         | Results displayed correctly            | Pass   |
| TC28         | Only one search condition is applied                          | Behavior is correct                    | Pass   |
| TC29         | All filters are cleared and all tasks are shown               | Filters cleared correctly              | Pass   |
| TC30         | Task data remains unchanged after refresh                     | Data remains unchanged                 | Pass   |
