import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import ProgrammInput from "./ProgramInput";
import "@testing-library/jest-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { act } from "react";

describe("ProgrammInput Component", () => {
	const mockOnProgramUploadClicked = vi.fn();

	beforeEach(() => {
		mockOnProgramUploadClicked.mockClear();
	});

	it("renders the component with default state", () => {
		act(() => {
			render(
				<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
			);
		});
		expect(screen.getByText("Type code")).toBeInTheDocument();
		expect(screen.getByText("File upload")).toBeInTheDocument();
		expect(screen.getByRole("textbox")).toBeInTheDocument();
		expect(screen.getByRole("button", { name: /upload/i })).toBeInTheDocument();
	});

	describe("type code", () => {
		it('allows typing in the textarea when "Type code" tab is selected', () => {
			act(() => {
				render(
					<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
				);
			});
			const textarea = screen.getByRole("textbox");
			act(() => {
				fireEvent.change(textarea, { target: { value: "Sample code" } });
			});
			expect(textarea).toHaveValue("Sample code");
		});

		it("calls onProgramUploadClicked with the correct program when upload button is clicked", async () => {
			act(() => {
				render(
					<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
				);
			});
			const textarea = screen.getByRole("textbox");
			const code = "Sample code";
			act(() => {
				fireEvent.change(textarea, { target: { value: code } });
				screen.getByRole("button", { name: /upload/i }).click();
			});
			await waitFor(() => {
				expect(mockOnProgramUploadClicked).toHaveBeenCalledWith(code);
			});
		});
	});

	describe("file input", () => {
		it('only displays file input when "File upload" tab is selected', async () => {
			act(() => {
				render(
					<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
				);
			});

			expect(screen.queryAllByRole("input")).toHaveLength(0);

			// Switch tab to file upload
			const fileUploadTabButton = screen.getByText("File upload");
			act(() => {
				// Explicitly need to focus first
				fileUploadTabButton.focus();
				fileUploadTabButton.click();
			});
			expect(screen.queryByRole("input")).toBeTruthy();

			// Switch tab to code entry
			const codeEntryTabButton = screen.getByText("Type code");
			act(() => {
				// Explicitly need to focus first
				codeEntryTabButton.focus();
				codeEntryTabButton.click();
			});
			expect(screen.queryAllByRole("input")).toHaveLength(0);
		});

		it("displays file content after uploading file", async () => {
			act(() => {
				render(
					<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
				);
			});

			// Switch tab
			const fileUploadTabButton = screen.getByText("File upload");
			act(() => {
				// Explicitly need to focus first
				fileUploadTabButton.focus();
				fileUploadTabButton.click();
			});

			// Mock file
			const fileInput = screen.getByRole("input") as HTMLInputElement;
			const fileContent = "File content";
			const file = {
				text: () => {
					return fileContent;
				},
			};
			Object.defineProperty(fileInput, "files", {
				value: [file],
			});
			act(() => {
				fireEvent.change(fileInput);
			});

			const textarea = screen.getByRole("textbox");
			await waitFor(() => {
				expect(textarea).toHaveValue(fileContent);
			});
		});

		it("emits correct event on programUploadClicked", async () => {
			act(() => {
				render(
					<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
				);
			});

			// Switch tab
			const fileUploadTabButton = screen.getByText("File upload");
			act(() => {
				// Explicitly need to focus first
				fileUploadTabButton.focus();
				fileUploadTabButton.click();
			});

			// Mock file
			const fileInput = screen.getByRole("input") as HTMLInputElement;
			const fileContent = "File content";
			const file = {
				text: () => {
					return fileContent;
				},
			};
			Object.defineProperty(fileInput, "files", {
				value: [file],
			});
			act(() => {
				fireEvent.change(fileInput);
			});

			// Make sure useState took effect
			const textarea = screen.getByRole("textbox");
			await waitFor(() => {
				expect(textarea).toHaveValue(fileContent);
			});

			// Upload
			act(() => {
				const btn = screen.getByRole("button", { name: /upload/i });
				btn.click();
			});
			await waitFor(() => {
				expect(mockOnProgramUploadClicked).toHaveBeenCalledWith(fileContent);
			});
		});
	});
});
