import { Editor, useMonaco } from "@monaco-editor/react";
import { MarkerSeverity } from "monaco-editor/esm/vs/editor/editor.api";
import { useEffect } from "react";

interface CodeEditorProps {
	setProgram: (s: string) => void;
}

export default function CodeEditor({ setProgram }: Readonly<CodeEditorProps>) {
	const monaco = useMonaco();
	const linterOwner = "redCodeLinter";

	const testProgram = `; heheheha, code here :D

	start   ADD #4, bomb_target   ; Increment bomb target by 4
        	MOV bomb, @bomb_target ; Place a bomb at the new target location
        	JMP start             ; Loop back to start

	bomb    	DAT #0                ; The bomb: will terminate an enemy process if hit
	bomb_target DAT #0            ; Starting location for bombing (relative to start)`;

	useEffect(() => {
		if (monaco) {
			//TODO: just for testing rn, will be deleted
			setUpTestingLinterWarningsAndErrors();
		}
	}, [monaco]);

	function setUpTestingLinterWarningsAndErrors() {
		setTimeout(
			() =>
				setLinterLinting(
					MarkerSeverity.Error,
					1,
					1,
					1,
					10,
					"This is an error1",
				),
			2000,
		);
	}

	function setLinterLinting(
		severity: MarkerSeverity,
		startLineNumber: number,
		endLineNumber: number,
		startColumn: number,
		endColumn: number,
		message: string,
	) {
		if (!monaco) return;
		const model = monaco.editor.getModels()[0];
		const marker = {
			severity: severity,
			startLineNumber: startLineNumber,
			startColumn: startColumn,
			endLineNumber: endLineNumber,
			endColumn: endColumn,
			message: message,
			owner: linterOwner,
			resource: model.uri,
		};
		const markers = monaco.editor.getModelMarkers({ owner: linterOwner });
		markers.push(marker);
		monaco.editor.setModelMarkers(model, linterOwner, markers);
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
