import { Editor, useMonaco } from "@monaco-editor/react";
import { useEffect } from "react";

interface CodeEditorProps {
	setProgram: (s: string) => void;
}

export default function CodeEditor({ setProgram }: Readonly<CodeEditorProps>) {
	const monaco = useMonaco();

	useEffect(() => {
		if (monaco) {
			// Register the custom language
			monaco.languages.register({ id: "myLang" });

			// Set up the language configuration
			monaco.languages.setLanguageConfiguration("myLang", {
				comments: {
					lineComment: "//",
					blockComment: ["/*", "*/"],
				},
				brackets: [
					["{", "}"],
					["[", "]"],
					["(", ")"],
				],
				autoClosingPairs: [
					{ open: "{", close: "}" },
					{ open: "[", close: "]" },
					{ open: "(", close: ")" },
					{ open: '"', close: '"' },
					{ open: "'", close: "'" },
				],
				surroundingPairs: [
					{ open: "{", close: "}" },
					{ open: "[", close: "]" },
					{ open: "(", close: ")" },
					{ open: '"', close: '"' },
					{ open: "'", close: "'" },
				],
			});

			// Define the language tokens and syntax highlighting
			monaco.languages.setMonarchTokensProvider("myLang", {
				tokenizer: {
					root: [
						// Keywords
						[/\b(keyword1|keyword2|keyword3)\b/, "keyword"],

						// Identifiers
						[/[a-z_$][\w$]*/, "identifier"],

						// Operators
						[/[=+\-*/]/, "operator"],

						// Numbers
						[/\d+/, "number"],

						// Strings
						[/".*?"/, "string"],
						[/'[^']*'/, "string"],
					],
				},
			});
		}
	}, [monaco]);

	return (
		<Editor
			height="30vh"
			theme="vs-dark"
			defaultLanguage="myLang"
			defaultValue="// heheheha, code here :D "
			onChange={(value) => setProgram(value ?? "")}
		/>
	);
}
