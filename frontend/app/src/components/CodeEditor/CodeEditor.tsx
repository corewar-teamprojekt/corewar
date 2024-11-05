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

	const keywordsList = [
		"MOV",
		"ADD",
		"SUB",
		"MUL",
		"DIV",
		"MOD",
		"JMP",
		"JMZ",
		"JMN",
		"DJN",
		"CMP",
		"SLT",
		"SPL",
		"DAT",
		"NOP",
	];

	useEffect(() => {
		if (monaco) {
			// Register the custom language
			monaco.languages.register({ id: "redcode" });
			//can't tell you why yet, but this way it do worky :sunglasses:
			setTimeout(setUpTokenProvider, 100);
			setTimeout(setUpAutoCompletion, 200);
			setTimeout(setUpTestingLinterWarningsAndErrors, 300);
		}
	}, [monaco]);

	function setUpTokenProvider() {
		if (!monaco) return;

		// Define the language tokens and syntax highlighting
		monaco.languages.setMonarchTokensProvider("redcode", {
			keywords: keywordsList,
			operators: ["#", "$", "@", "<", ">", "{", "}"],
			symbols: /[@#$,]/,

			// Define tokenizer rules
			tokenizer: {
				root: [
					// Comments
					[/(;.*$)/, "comment"],

					// Labels
					[/^[a-zA-Z_]\w*:/, "type.identifier"],

					// Instructions (keywords)
					[
						/\b(MOV|ADD|SUB|MUL|DIV|MOD|JMP|JMZ|JMN|DJN|CMP|SLT|SPL|DAT|NOP)\b/,
						"keyword",
					],

					// Numbers
					[/\b\d+\b/, "number"],

					// Operators (e.g., addressing modes)
					[/[#$@<>]/, "operator"],

					// Any other symbol
					[/[.,]/, "delimiter"],
				],
			},
		});
	}

	function setUpAutoCompletion() {
		if (!monaco) return;
		const model = monaco.editor.getModels()[0];
		monaco.languages.registerCompletionItemProvider("redcode", {
			provideCompletionItems: (_, position) => {
				// Get the word until the current position to find the appropriate range
				const wordInfo = model.getWordUntilPosition(position);

				// Define the range for the completion item
				const range = new monaco.Range(
					position.lineNumber,
					wordInfo.startColumn,
					position.lineNumber,
					wordInfo.endColumn,
				);
				const suggestions = keywordsList.map((keyword) => ({
					label: keyword,
					kind: monaco.languages.CompletionItemKind.Keyword,
					insertText: keyword,
					range: range,
				}));
				return { suggestions };
			},
		});
	}

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
