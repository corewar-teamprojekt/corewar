import { Editor, useMonaco } from "@monaco-editor/react";
import { useEffect } from "react";

interface CodeEditorProps {
	setProgram: (s: string) => void;
}

export default function CodeEditor({ setProgram }: Readonly<CodeEditorProps>) {
	const monaco = useMonaco();

	const testProgram = `; heheheha, code here :D

	start   ADD #4, bomb_target   ; Increment bomb target by 4
        	MOV bomb, @bomb_target ; Place a bomb at the new target location
        	JMP start             ; Loop back to start

	bomb    	DAT #0                ; The bomb: will terminate an enemy process if hit
	bomb_target DAT #0            ; Starting location for bombing (relative to start)`;

	useEffect(() => {
		if (monaco) {
			setTimeout(setUpTestingLinterWarningsAndErrors, 500);
		}
	}, [monaco]);

	function setUpTestingLinterWarningsAndErrors() {
		if (!monaco) return;
		const model = monaco.editor.getModels()[0];
		const markers = [
			{
				severity: monaco.MarkerSeverity.Warning,
				startLineNumber: 2,
				startColumn: 1,
				endLineNumber: 2,
				endColumn: 50,
				message: "whoopsies there is an wornin in line 2 >w<",
			},
			{
				severity: monaco.MarkerSeverity.Error,
				startLineNumber: 4,
				startColumn: 1,
				endLineNumber: 4,
				endColumn: 50,
				message: "whoopsies there is an ewwor in line 4 >w<",
			},
		];

		// Set markers on the model
		monaco.editor.setModelMarkers(model, "customLinter", markers);
	}

	return (
		<Editor
			height="60vh"
			theme="vs-dark"
			defaultLanguage="redcode"
			defaultValue={testProgram}
			onChange={(value) => setProgram(value ?? "")}
		/>
	);
}
