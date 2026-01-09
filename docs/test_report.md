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