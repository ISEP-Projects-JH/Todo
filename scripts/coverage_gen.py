import csv
import sys
from pathlib import Path

CSV_PATH = Path("target/site/jacoco/jacoco.csv")


def percent(covered: int, missed: int) -> str:
    total = covered + missed
    if total == 0:
        return "100.00%"
    return f"{covered / total * 100:.2f}%"


def main():
    if not CSV_PATH.exists():
        print(f"JaCoCo CSV not found: {CSV_PATH}", file=sys.stderr)
        sys.exit(1)

    rows = []

    total_instruction_covered = 0
    total_instruction_missed = 0

    with CSV_PATH.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for r in reader:
            covered = int(r["INSTRUCTION_COVERED"])
            missed = int(r["INSTRUCTION_MISSED"])

            total_instruction_covered += covered
            total_instruction_missed += missed

            coverage = percent(covered, missed)

            rows.append([
                r["GROUP"],
                r["PACKAGE"],
                r["CLASS"],
                coverage,
                r["INSTRUCTION_MISSED"],
                r["INSTRUCTION_COVERED"],
                r["BRANCH_MISSED"],
                r["BRANCH_COVERED"],
                r["LINE_MISSED"],
                r["LINE_COVERED"],
                r["COMPLEXITY_MISSED"],
                r["COMPLEXITY_COVERED"],
                r["METHOD_MISSED"],
                r["METHOD_COVERED"],
            ])

    total_coverage = percent(
        total_instruction_covered,
        total_instruction_missed,
    )

    headers = [
        "GROUP",
        "PACKAGE",
        "CLASS",
        "COVERAGE",
        "INSTRUCTION_MISSED",
        "INSTRUCTION_COVERED",
        "BRANCH_MISSED",
        "BRANCH_COVERED",
        "LINE_MISSED",
        "LINE_COVERED",
        "COMPLEXITY_MISSED",
        "COMPLEXITY_COVERED",
        "METHOD_MISSED",
        "METHOD_COVERED",
    ]

    print("## JaCoCo Coverage Summary\n")
    print("| " + " | ".join(headers) + " |")
    print("|" + "|".join(["---"] * len(headers)) + "|")

    for r in rows:
        print("| " + " | ".join(r) + " |")

    print("| TOTAL | - | - | "
          f"{total_coverage} | "
          f"{total_instruction_missed} | "
          f"{total_instruction_covered} | "
          "- | - | - | - | - | - | - | - |")


if __name__ == "__main__":
    main()

