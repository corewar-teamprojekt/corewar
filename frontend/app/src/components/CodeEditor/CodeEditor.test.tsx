import { BASE_POLLING_INTERVAL_MS } from "@/consts";
import { usePageVisibility } from "@/lib/usePageVisibility";
import { getLinterLintsV1 } from "@/services/rest/RestService";
import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
} from "@testing-library/react";
import { beforeEach, describe, expect, it, Mock, vi } from "vitest";
import CodeEditor from "./CodeEditor";
import { Linterlint } from "@/domain/LinterLint";

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
		render(<CodeEditor setProgram={setProgram} program="initial value" />);
		const textarea = screen.getByRole("textbox");

		act(() => {
			fireEvent.change(textarea, { target: { value: "new content" } });
		});
		expect(setProgram).toHaveBeenCalledTimes(2); // one for initial value and one for the new content
		expect(setProgram).toHaveBeenCalledWith("new content");
	});

	it("sets polled linterLints correctly", async () => {
		const linterLints: Linterlint[] = [
			{
				line: 1,
				columnStart: 1,
				columnEnd: 5,
				message: "Test message",
			},
		];
		(getLinterLintsV1 as Mock).mockResolvedValue(linterLints);
		(usePageVisibility as Mock).mockReturnValue(true);

		render(<CodeEditor setProgram={setProgram} program="initial value" />);

		waitFor(
			() => {
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
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 500 },
		);
	});

	it("uploads file content correctly", async () => {
		render(<CodeEditor setProgram={setProgram} program="initial value" />);
		const file = new File(["file content"], "test.txt", { type: "text/plain" });

		act(() => {
			screen.getByText("File").click();
		});
		const input = screen.getByLabelText("upload");

		await act(async () => {
			fireEvent.change(input, { target: { files: [file] } });
		});

		waitFor(() => expect(setProgram).toHaveBeenCalledWith("file content"));
	});

	it("downloads file content correctly", async () => {
		const expectedCode = "initial value";

		//this needs to be mocked, since otherwise it is not correctly implemented and causes an error
		global.URL.createObjectURL = vi.fn();

		render(<CodeEditor setProgram={setProgram} program={expectedCode} />);
		act(() => {
			screen.getByText("File").click();
		});
		const downloadButton = screen.getByLabelText("download");

		const createElementSpy = vi.spyOn(document, "createElement");
		const appendChildSpy = vi.spyOn(document.body, "appendChild");
		const removeChildSpy = vi.spyOn(document.body, "removeChild");

		await act(async () => {
			fireEvent.click(downloadButton);
		});

		waitFor(() => {
			expect(createElementSpy).toHaveBeenCalledWith("a");
			expect(appendChildSpy).toHaveBeenCalled();
			expect(removeChildSpy).toHaveBeenCalled();
			const link = createElementSpy.mock.results[0].value;
			expect(link.href).toContain(encodeURIComponent(expectedCode));
		});
	});
});
