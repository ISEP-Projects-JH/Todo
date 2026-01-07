from datetime import datetime

def main():
    now = datetime.utcnow().isoformat()

    with open("output.md", "w", encoding="utf-8") as f:
        f.write("# Auto Test Result\n\n")
        f.write(f"- Time: {now}\n")
        f.write("- Status: success\n")

    print("success")


if __name__ == "__main__":
    main()
