# Change into the backend directory and run gradle for Unix and Windows inside the backend directory.

import os
import sys
import subprocess


def run_gradle():
    args = sys.argv
    if len(args) < 2:
        print("Usage: python run_gradle.py <task>")
        sys.exit(1)

    os.chdir("backend")

    task = args[1]
    gradle = "./gradlew" if os.name == "posix" else ".\gradlew.bat"
    if not os.path.exists(gradle):
        print("gradlew not found")
        sys.exit(1)

    cmd = [gradle, task]
    print("Running gradle task: " + task)
    try:
        subprocess.run(cmd, check=True)
    except Exception:
        sys.exit(1)


if __name__ == "__main__":
    run_gradle()
