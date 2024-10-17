import { render, screen, fireEvent } from "@testing-library/react";
import ProgrammInput from "./ProgramInput";
import "@testing-library/jest-dom";
import { beforeEach, describe, expect, test, vi } from "vitest";

describe("ProgrammInput Component", () => {
	const mockOnProgramUploadClicked = vi.fn();

	beforeEach(() => {
		mockOnProgramUploadClicked.mockClear();
	});

	test("renders the component with default state", () => {
		render(
			<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
		);
		expect(screen.getByText("Type code")).toBeInTheDocument();
		expect(screen.getByText("File upload")).toBeInTheDocument();
		expect(screen.getByRole("textbox")).toBeInTheDocument();
		expect(screen.getByRole("button", { name: /upload/i })).toBeInTheDocument();
	});

	test('allows typing in the textarea when "Type code" tab is selected', () => {
		render(
			<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
		);
		const textarea = screen.getByRole("textbox");
		fireEvent.change(textarea, { target: { value: "Sample code" } });
		expect(textarea).toHaveValue("Sample code");
	});

	// test('displays file input when "File upload" tab is selected', async () => {
	// 	render(
	// 		<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
	// 	);
	//     fireEvent.click(screen.getByText("File upload"));
	// 	expect(screen.getByLabelText(/file/i)).toBeInTheDocument();
	// });

	test("calls onProgramUploadClicked with the correct program when upload button is clicked", () => {
		render(
			<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
		);
		const textarea = screen.getByRole("textbox");
		fireEvent.change(textarea, { target: { value: "Sample code" } });
		fireEvent.click(screen.getByRole("button", { name: /upload/i }));
		expect(mockOnProgramUploadClicked).toHaveBeenCalledWith("Sample code");
	});

	// test("reads file content and sets it as program when a file is uploaded", async () => {
	// 	render(
	// 		<ProgrammInput onProgramUploadClicked={mockOnProgramUploadClicked} />,
	// 	);
	// 	fireEvent.click(screen.getByText("File upload"));
	// 	const fileInput = screen.getByLabelText(/file/i);
	// 	const file = new File(["File content"], "test.txt", { type: "text/plain" });

	// 	Object.defineProperty(fileInput, "files", {
	// 		value: [file],
	// 	});

	// 	fireEvent.change(fileInput);
	// 	const textarea = await screen.findByRole("textbox");
	// 	expect(textarea).toHaveValue("File content");
	// });
});
