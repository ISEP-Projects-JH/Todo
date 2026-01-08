from datetime import datetime

from jh_utils.ostream import IOS, ofs_open

import requests

from selenium import webdriver
from selenium.webdriver import Keys
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager


def main():
    now = datetime.utcnow().isoformat()

    with ofs_open("output.md", mode=IOS.OUT, encoding="utf-8") as s:
        s << "# Auto Test Result\n\n" \
        << f"- Time: {now}\n" \
        << "- Status: success\n"

    print("success")


if __name__ == "__main__":
    main()
