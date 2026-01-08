from datetime import datetime, timezone, timedelta
from typing import Any, Optional

import requests
from jh_utils.ostream import IOS, ofs_open, ostring, OStringStream
from selenium import webdriver
from selenium.common.exceptions import (
    NoSuchElementException,
    WebDriverException,
    TimeoutException, StaleElementReferenceException,
)
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager


class AutoScriptError(Exception):
    def what(self):
        return str(self)


class TodoView:
    def __init__(self, title: str, tags: list[str], description: str):
        self.title = title
        self.tags = tags
        self.description = description

    def __repr__(self) -> str:
        return (
            f"TodoView("
            f"title={self.title!r}, "
            f"tags={self.tags!r}, "
            f"description={self.description!r}"
            f")"
        )

    def __eq__(self, other) -> bool:
        if not isinstance(other, TodoView):
            return NotImplemented
        return (
                self.title == other.title
                and set(self.tags) == set(other.tags)
                and self.description == other.description
        )

    def __hash__(self) -> int:
        return hash((
            self.title,
            self.description,
            frozenset(self.tags),
        ))

    @staticmethod
    def make(
            title: str,
            description: str,
            tags_raw: str
    ) -> 'TodoView':
        tags = [
            t.strip()
            for t in tags_raw.split(",")
            if t.strip()
        ]

        return TodoView(
            title=title,
            tags=tags,
            description=description
        )


BASE_URL = "http://localhost:3000"
API_BASE = "http://localhost:8000"
SUCCESS_PREFIX = "- [x] "
FAILED_PREFIX = "- [ ] "
EXPECTED_PREFIX = "  * Expected: "
RESULT_PREFIX = "  * Result: "

options = Options()
options.add_argument("--headless")
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

oss = ostring()

# ---------------- selectors ----------------
CREATE_FORM = (By.CSS_SELECTOR, "div.create-form")
TODO_LIST = (By.CSS_SELECTOR, "div.todo-list")
TODO_ITEMS = (By.CSS_SELECTOR, "div.todo-item")

TEXT_SEARCH = (By.XPATH, "//input[@placeholder='Type and press Enter...']")
DATE_SEARCH = (By.CSS_SELECTOR, "input.date-input")
TAG_SEARCH = (By.XPATH, "//input[@placeholder='Enter one tag...']")
RESET_BTN = (By.CSS_SELECTOR, "button.reset-btn")


# ---------------- helpers ----------------
def add_todo(driver_, title, desc, tags):
    form = driver_.find_element(*CREATE_FORM)

    title_input = form.find_element(By.XPATH, ".//input[@placeholder='Task title']")
    desc_input = form.find_element(By.XPATH, ".//input[@placeholder='Description (optional)']")
    tag_input = form.find_element(By.XPATH, ".//input[@placeholder='Tags (comma separated)']")

    title_input.clear()
    desc_input.clear()
    tag_input.clear()

    title_input.send_keys(title)
    desc_input.send_keys(desc)
    tag_input.send_keys(tags)

    form.find_element(By.CSS_SELECTOR, ".add-btn").click()

    WebDriverWait(driver_, 5).until(EC.presence_of_element_located(TODO_ITEMS))

    return TodoView.make(title, desc, tags)


def reset_search(driver_):
    driver_.find_element(*RESET_BTN).click()
    WebDriverWait(driver_, 10).until(EC.presence_of_element_located(TODO_LIST))


def parse_todo_item(item_) -> TodoView:
    title = item_.find_element(By.CSS_SELECTOR, ".title").text
    tags = [t.text for t in item_.find_elements(By.CSS_SELECTOR, ".tag")]

    desc_elems = item_.find_elements(By.CSS_SELECTOR, ".description")
    description = desc_elems[0].text if desc_elems else ""

    return TodoView(
        title=title,
        tags=tags,
        description=description
    )


def fetch_todos(driver_):
    items = driver_.find_elements(*TODO_ITEMS)
    return [parse_todo_item(i) for i in items]


def clear_all_todos():
    resp = requests.delete(f"{API_BASE}/api/todos")
    if int(resp.status_code) >= 400:
        raise ValueError


def append_result(
        ost: OStringStream,
        base: str,
        expected: Any,
        result: Optional[Any]
) -> bool:
    if result is None:
        success = False
    else:
        success = result == expected

    if success:
        ost << SUCCESS_PREFIX << base << "\n"
    else:
        ost << FAILED_PREFIX << base << "\n"
        ost << EXPECTED_PREFIX << str(expected) << "\n"
        ost << RESULT_PREFIX << str(result) << "\n"

    return success


def snapshot_dashboard_and_back(driver_) -> tuple[Optional[int], Optional[int]]:
    current_url = driver_.current_url
    driver_.get(BASE_URL)

    def read_nums():
        try:
            p = int(driver_.find_element(
                By.XPATH,
                "//div[contains(@class,'stat-card') and contains(@class,'pending')]//div[@class='number']"
            ).text)
            c = int(driver_.find_element(
                By.XPATH,
                "//div[contains(@class,'stat-card') and contains(@class,'completed')]//div[@class='number']"
            ).text)
            return p, c
        except (WebDriverException, ValueError):
            return None, None

    last = None
    for _ in range(10):
        cur = read_nums()
        if cur == last and cur != (None, None):
            break
        last = cur
        WebDriverWait(driver_, 0.5)

    pending_num, completed_num = last

    driver_.get(current_url)
    WebDriverWait(driver_, 10).until(EC.url_to_be(current_url))

    return pending_num, completed_num


def main():
    now = datetime.now(timezone.utc).isoformat()

    failed: int = 0

    todos: dict[str, TodoView] = {}

    try:
        try:
            clear_all_todos()
        except (requests.exceptions.RequestException, ValueError):
            raise AutoScriptError("Unable to clear DataBase")

        access_granted: bool = True
        try:
            driver.get(BASE_URL)
        except (TimeoutException, WebDriverException):
            access_granted = False

        if not append_result(oss, f"Access to Base URL {BASE_URL}",
                             True, access_granted):
            raise AutoScriptError("Access denied")

        pending_num: Optional[int]
        completed_num: Optional[int]

        expected_pending: int = 0
        expected_completed: int = 0

        try:
            pending_text = driver.find_element(
                By.XPATH,
                "//div[contains(@class,'stat-card') and contains(@class,'pending')]//div[@class='number']"
            ).text

            completed_text = driver.find_element(
                By.XPATH,
                "//div[contains(@class,'stat-card') and contains(@class,'completed')]//div[@class='number']"
            ).text

            pending_num = int(pending_text)
            completed_num = int(completed_text)

        except (WebDriverException, ValueError):
            pending_num = None
            completed_num = None

        if not append_result(oss, f"Pending and Completed inited as {expected_pending, expected_completed}",
                             (expected_pending, expected_completed), (pending_num, completed_num)):
            failed += 1

        todos_url: Optional[str]

        try:
            driver.find_element(By.LINK_TEXT, "Todos").click()

            WebDriverWait(driver, 10).until(
                EC.url_contains("/todos")
            )

            todos_url = driver.current_url

        except WebDriverException:
            todos_url = None

        if not append_result(oss, f"Redirect to {BASE_URL}/todos",
                             f"{BASE_URL}/todos", todos_url):
            raise AutoScriptError(f"Failed to redirect to {BASE_URL}/todos")

        try:
            todos["apple"] = add_todo(driver, "task apple", "desc1", "fruit,red")
            todos["banana"] = add_todo(driver, "task banana", "desc2", "fruit,yellow")
            todos["car"] = add_todo(driver, "car task", "desc3", "vehicle")
            todos["sth_el"] = add_todo(driver, "something else", "desc4", "")
            reset_search(driver)
            expected_pending += 4
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Add Todo Failed")

        (oss << "> Adding Todos:  \n\n| Item | Todo |\n|---------|------|" <<
         ("".join(f"\n| {key} | {val} | " for key, val in todos.items())) << "\n")

        pending_num, completed_num = snapshot_dashboard_and_back(driver)

        if not append_result(oss, f"Add {expected_pending} Todo(s)",
                             (expected_pending, expected_completed), (pending_num, completed_num)):
            raise AutoScriptError("Add Todo Failed")

        try:
            reset_search(driver)

            text_input = driver.find_element(*TEXT_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.focus();
            el.value = 'task';
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new KeyboardEvent('keyup', {
              bubbles: true,
              key: 'Enter',
              code: 'Enter',
              keyCode: 13
            }));
            """, text_input)

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
            )
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Search Todo by Text Failed")

        res = set(fetch_todos(driver))

        if not append_result(oss, f"Search Todo by Text \"task\"",
                             {todos.get("apple"), todos.get("banana"), todos.get("car")}, res):
            failed += 1

        try:
            reset_search(driver)

            text_input = driver.find_element(*TEXT_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.focus();
            el.value = 'some';
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new KeyboardEvent('keyup', {
              bubbles: true,
              key: 'Enter',
              code: 'Enter',
              keyCode: 13
            }));
            """, text_input)

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
            )
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Search Todo by Text Failed")

        res = set(fetch_todos(driver))

        if not append_result(oss, f"Search Todo by Text \"some\"",
                             {todos.get("sth_el")}, res):
            failed += 1

        try:
            reset_search(driver)
            tag_input = driver.find_element(*TAG_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.focus();
            el.value = 'fruit';
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new KeyboardEvent('keyup', {
              bubbles: true,
              key: 'Enter',
              code: 'Enter',
              keyCode: 13
            }));
            """, tag_input)

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
            )
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Search Todo by Tag Failed")

        res = set(fetch_todos(driver))

        if not append_result(oss, f"Search Todo by Tag \"fruit\"",
                             {todos.get("apple"), todos.get("banana")}, res):
            failed += 1
        try:
            reset_search(driver)
            tag_input = driver.find_element(*TAG_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.focus();
            el.value = 'no_such_tag';
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new KeyboardEvent('keyup', {
              bubbles: true,
              key: 'Enter',
              code: 'Enter',
              keyCode: 13
            }));
            """, tag_input)

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) == 0
            )
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Search Todo by Tag Failed")

        res = set(fetch_todos(driver))

        if not append_result(oss, f"Search Todo by Tag \"no_such_tag\"",
                             set(), res):
            failed += 1

        today = datetime.today().date()
        past = (today - timedelta(days=30)).isoformat()
        future = (today + timedelta(days=30)).isoformat()

        try:
            reset_search(driver)
            date_input = driver.find_element(*DATE_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.value = arguments[1];
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new Event('change', { bubbles: true }));
            """, date_input, past)

            WebDriverWait(driver, 5).until(lambda d: len(d.find_elements(*TODO_ITEMS)) == 0)
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Search Todo by Time Failed")

        res = set(fetch_todos(driver))
        if not append_result(oss, f"Search Todo before Time {past}(last month)",
                             set(), res):
            failed += 1

        try:
            reset_search(driver)
            date_input = driver.find_element(*DATE_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.value = arguments[1];
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new Event('change', { bubbles: true }));
            """, date_input, future)

            WebDriverWait(driver, 5).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, ".todo-item"))
            )
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Search Todo by Time Failed")

        res = set(fetch_todos(driver))
        if not append_result(oss, f"Search Todo before Time {future}(next month)",
                             set(todos.values()), res):
            failed += 1

        try:
            reset_search(driver)

            text_input = driver.find_element(*TEXT_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.focus();
            el.value = 'something';
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new KeyboardEvent('keyup', {
              bubbles: true,
              key: 'Enter',
              code: 'Enter',
              keyCode: 13
            }));
            """, text_input)

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
            )
            item = driver.find_elements(*TODO_ITEMS)[0]
            item.find_element(By.CSS_SELECTOR, ".edit-btn").click()

            edit_form = WebDriverWait(driver, 5).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, ".edit-form"))
            )

            inputs = edit_form.find_elements(By.CSS_SELECTOR, ".edit-input")
            inputs[0].clear()
            inputs[0].send_keys("edited desc")
            inputs[1].clear()
            inputs[1].send_keys("editedtag")
            edit_form.find_element(By.CSS_SELECTOR, ".save-btn").click()

            WebDriverWait(driver, 5).until(
                EC.presence_of_element_located(
                    (By.XPATH, "//div[@class='description' and text()='edited desc']")
                )
            )
            reset_search(driver)

            text_input = driver.find_element(*TEXT_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.focus();
            el.value = 'something';
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new KeyboardEvent('keyup', {
              bubbles: true,
              key: 'Enter',
              code: 'Enter',
              keyCode: 13
            }));
            """, text_input)

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
            )
        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Search Todo by Text Failed")

        sth_el = todos.get("sth_el")
        sth_el.description = "edited desc"
        sth_el.tags = ["editedtag"]

        res_ele = fetch_todos(driver)[0]

        if not append_result(oss, f"Edit the first Todo begins with \"something\"",
                             sth_el, res_ele):
            failed += 1

        def _click_vehicle():
            try:
                reset_search(driver)
                tag_input_ = driver.find_element(*TAG_SEARCH)

                driver.execute_script("""
                const el = arguments[0];
                el.focus();
                el.value = 'vehicle';
                el.dispatchEvent(new Event('input', { bubbles: true }));
                el.dispatchEvent(new KeyboardEvent('keyup', {
                  bubbles: true,
                  key: 'Enter',
                  code: 'Enter',
                  keyCode: 13
                }));
                """, tag_input_)

                WebDriverWait(driver, 5).until(
                    lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
                )

                item_ = driver.find_elements(*TODO_ITEMS)[0]
                checkbox = item_.find_element(By.CSS_SELECTOR, "input[type='checkbox']")

            except (TimeoutException, NoSuchElementException):
                raise AutoScriptError("Find Todo by Tag 'vehicle' Failed")

            checkbox.click()

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
            )

        _click_vehicle()
        pending_num, completed_num = snapshot_dashboard_and_back(driver)

        expected_pending -= 1
        expected_completed += 1

        if not append_result(
                oss,
                "Toggle vehicle todo to completed",
                (expected_pending, expected_completed),
                (pending_num, completed_num),
        ):
            failed += 1

        _click_vehicle()
        pending_num, completed_num = snapshot_dashboard_and_back(driver)

        expected_pending += 1
        expected_completed -= 1

        if not append_result(
                oss,
                "Toggle vehicle todo to pending",
                (expected_pending, expected_completed),
                (pending_num, completed_num),
        ):
            failed += 1

        def safe_todo_titles(driver_):
            try:
                return [
                    e_.text
                    for e_ in driver_.find_elements(
                        By.CSS_SELECTOR, ".todo-item .title"
                    )
                ]
            except StaleElementReferenceException:
                return None
        try:
            reset_search(driver)

            text_input = driver.find_element(*TEXT_SEARCH)

            driver.execute_script("""
            const el = arguments[0];
            el.focus();
            el.value = 'else';
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new KeyboardEvent('keyup', {
              bubbles: true,
              key: 'Enter',
              code: 'Enter',
              keyCode: 13
            }));
            """, text_input)

            WebDriverWait(driver, 5).until(
                lambda d: len(d.find_elements(*TODO_ITEMS)) > 0
            )

            item = driver.find_elements(*TODO_ITEMS)[0]
            title_text = item.find_element(By.CSS_SELECTOR, ".title").text

            item.find_element(By.CSS_SELECTOR, ".delete-btn").click()

            WebDriverWait(driver, 5).until(
                lambda d: (
                        (titles := safe_todo_titles(d)) is not None
                        and title_text not in titles
                )
            )

        except (TimeoutException, NoSuchElementException):
            raise AutoScriptError("Delete Todo with text 'else' Failed")

        pending_num, completed_num = snapshot_dashboard_and_back(driver)

        expected_pending -= 1

        if not append_result(
                oss,
                "Delete the first Todo with text \"else\"",
                (expected_pending, expected_completed),
                (pending_num, completed_num),
        ):
            failed += 1

    except AutoScriptError as e:
        failed += 1
        oss << (f"\n### Failed with Error:\n<span style=\"font-size:1.4em; "
                f"color:red;\">\n  {e.what()}\n</span>")

    finally:
        with ofs_open("output.md", mode=IOS.OUT, encoding="utf-8") as s:
            s << "# Auto Test Result\n\n" << f"- Time: {now}\n" \
            << f"- Status: {('Success' if not failed else 'Failed')}\n\n" \
            << f"- Total failed {failed}\n\n" \
            << "## Test Cases:\n" \
            << str(oss)

    print("success" if not failed else "failed")


if __name__ == "__main__":
    main()
