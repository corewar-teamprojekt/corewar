import unittest
from commit_msg_linter import (
    test,
)  # Adjust this import based on your script's structure


class TestCommitMsgLinter(unittest.TestCase):
    def test_valid_single_id(self):
        self.assertTrue(test("[#123] message"))

    def test_valid_multiple_ids(self):
        self.assertTrue(test("[#123,#456] message"))

    def test_valid_multiple_ids_with_space(self):
        self.assertTrue(test("[#123, #456] message"))

    def test_valid_multiple_words(self):
        self.assertTrue(test("[#123] test message"))

    def test_invalid_no_space_after_bracket(self):
        self.assertFalse(test("[#123]message"))

    def test_invalid_space_before_id(self):
        self.assertFalse(test("[ #123 ] message"))

    def test_invalid_empty_message(self):
        self.assertFalse(test("[#123] "))

    def test_invalid_missing_hash(self):
        self.assertFalse(test("[123] message"))

    def test_invalid_no_brackets(self):
        self.assertFalse(test("#123 message"))

    def test_invalid_only_brackets(self):
        self.assertFalse(test("[] message"))

    def test_invalid_multiple_spaces(self):
        self.assertFalse(test("[#123]  message"))

    def test_valid_large_id(self):
        self.assertTrue(test("[#123456789] message"))

    def test_valid_multiple_large_ids(self):
        self.assertTrue(test("[#123456789,#987654321] message"))

    def test_invalid_extra_comma(self):
        self.assertFalse(test("[#123,,#456] message"))

    def test_invalid_multiple_commas(self):
        self.assertFalse(test("[#123,#456,#] message"))

    def test_invalid_id_with_alphabets(self):
        self.assertFalse(test("[#abc] message"))


if __name__ == "__main__":
    unittest.main()
