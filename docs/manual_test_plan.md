# Manual Test Plan

**Todo Application**

---

## 1. Application Description

The Todo Application is a web-based task management system that allows users to create, view, edit, complete, and delete tasks.
The application provides two main pages: **Dashboard** and **Todos**.

* The **Dashboard** page displays task statistics, including the number of pending and completed tasks.
* The **Todos** page allows users to manage tasks, including adding new tasks, editing existing tasks, marking tasks as completed, deleting tasks, and searching or filtering tasks.

The application is designed to help users organize daily activities efficiently through a simple and intuitive interface.

---

## 2. Testing Scope

The scope of this manual testing campaign includes functional testing of the core features of the Todo Application.

### In scope:

* Page loading and navigation between Dashboard and Todos pages
* Task creation with different input conditions
* Task editing, completion, and deletion
* Dashboard statistics calculation and update
* Search and filter functionalities
* Data persistence after page refresh
* Basic validation and error handling

### Out of scope:

* Performance testing
* Security testing
* Cross-browser compatibility testing
* Mobile responsiveness testing

---

## 3. Testing Objectives

The objectives of this testing campaign are:

* To verify that all main functional features work as expected
* To ensure correct navigation and stable page behavior
* To validate task management operations (create, edit, complete, delete)
* To confirm that Dashboard statistics accurately reflect task status
* To identify functional defects and usability issues
* To ensure the application behaves correctly in invalid or edge-case scenarios

---

## 4. Acceptance Criteria

The application is considered acceptable if:

* All high-priority test cases pass successfully
* Core functionalities (task management and navigation) behave correctly
* Dashboard statistics are consistent with task data
* No critical functional defects are present
* The application remains stable during normal usage

Minor usability issues may be accepted if they do not affect core functionality.

---

## 5. Features to Be Tested

The following features are covered by this testing campaign:

1. Dashboard page loading and statistics display
2. Todos page loading
3. Navigation between Dashboard and Todos
4. Task creation
5. Task editing
6. Task completion and uncompletion
7. Task deletion
8. Dashboard statistics update
9. Task search by text
10. Task filtering by date
11. Task filtering by tag
12. Reset filter functionality
13. Data persistence after page refresh
14. Basic input validation and error handling

---

## 6. Test Environment

* **Application Type**: Web application
* **Browser**: Google Chrome
* **Operating System**: Desktop environment
* **Test Data**: Manually created tasks during testing
* **Network**: Local or stable internet connection

---

## 7. Manual Test Cases

Manual functional testing based on predefined test cases.
Detailed manual test cases are documented in a separate file.