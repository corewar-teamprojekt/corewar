import sys
import re


def test(commit_msg: str) -> bool:
    # Define the regex pattern
    pattern = r"^\[\#\d+(, \#\d+)*\] \w.*"

    return bool(re.match(pattern, commit_msg))


if __name__ == "__main__":
    commit_msg_file = sys.argv[1]
    print(sys.argv)
    print()
    print(sys.stdin.read().strip())
    with open(commit_msg_file, "r") as file:
        commit_msg = file.read().strip()

    print(commit_msg)

    if not test(commit_msg):
        print("Commit message does not meet the required format.")
        print("Allowed formats:")
        print("[#123] message")
        print("[#123,#456] message")
        print("[#123, #456] message")
        sys.exit(1)
