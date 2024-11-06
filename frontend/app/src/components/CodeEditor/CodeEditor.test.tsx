import { usePageVisibility } from "@/lib/usePageVisibility";
import { getLinterLintsV1 } from "@/services/rest/RestService";
import { act, fireEvent, render, screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, Mock, vi } from "vitest";
import CodeEditor from "./CodeEditor";

vi.mock("@/lib/usePageVisibility");
vi.mock("@/services/rest/RestService");

//mock the monaco editor implementation with generic textarea, since the functionality of this imported editor should not be tested

const mockEditor = {
	getModels: () => [
		{
			getValue: () => "editor content",
			uri: "model-uri",
		},
	],
	setModelMarkers: vi.fn(),
};

vi.mock("@monaco-editor/react", () => ({
	Editor: vi.fn(({ onMount, onChange }) => {
		onMount({
			getValue: () => "initial value",
		});
		return <textarea onChange={(e) => onChange(e.target.value)} />;
	}),
	useMonaco: () => ({
		editor: mockEditor,
	}),
}));

describe("CodeEditor", () => {
	const setProgram = vi.fn();

	beforeEach(() => {
		vi.clearAllMocks();
	});

	it("calls setProgram after every input change", async () => {
		render(<CodeEditor setProgram={setProgram} />);
		const textarea = screen.getByRole("textbox");

		act(() => {
			fireEvent.change(textarea, { target: { value: "new content" } });
		});
		expect(setProgram).toHaveBeenCalledTimes(2); // one for initial value and one for the new content
		expect(setProgram).toHaveBeenCalledWith("new content");
	});

	it("sets polled linterLints correctly", async () => {
		const linterLints = [
			{
				severity: 1,
				startLineNumber: 1,
				startColumn: 1,
				endLineNumber: 1,
				endColumn: 5,
				message: "Test message",
			},
		];
		(getLinterLintsV1 as Mock).mockResolvedValue(linterLints);
		(usePageVisibility as Mock).mockReturnValue(true);

		render(<CodeEditor setProgram={setProgram} />);

		// Wait for the polling to occur
		await new Promise((resolve) => setTimeout(resolve, 2000));

		expect(getLinterLintsV1).toHaveBeenCalled();
		expect(mockEditor.setModelMarkers).toHaveBeenCalledWith(
			expect.anything(),
			"redCodeLinter",
			expect.arrayContaining([
				expect.objectContaining({
					message: "Test message",
				}),
			]),
		);
	});
});
