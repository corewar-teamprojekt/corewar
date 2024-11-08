import "@testing-library/jest-dom";
import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
} from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ProgrammInput from "./ProgramInput";

describe("ProgrammInput", () => {
	const mockOnProgramUploadClicked = vi.fn();

	beforeEach(() => {
		mockOnProgramUploadClicked.mockClear();
	});

	it("calls onProgramUploadClicked with the correct program when upload button is clicked", async () => {
		// mock code editor into being a generic textarea
		vi.mock("../CodeEditor/CodeEditor", () => ({
			default: vi.fn(({ setProgram }) => {
				return <textarea onChange={(e) => setProgram(e.target.value)} />;
			}),
		}));
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
