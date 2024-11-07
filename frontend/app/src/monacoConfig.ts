import { loader } from "@monaco-editor/react";

import * as monaco from "monaco-editor";
import editorWorker from "monaco-editor/esm/vs/editor/editor.worker?worker";

const upperkKeywordsList = [
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
	"LDP",
	"SEQ",
	"SNE",
	"STP",
];

const fullKeywordsList = [...upperkKeywordsList];

[...upperkKeywordsList].forEach((keyword) =>
	fullKeywordsList.push(keyword.toLowerCase()),
);

const keywordListRegex = new RegExp(fullKeywordsList.join("|"), "g");

self.MonacoEnvironment = {
	getWorker() {
		return new editorWorker();
	},
};

monaco.languages.register({ id: "redcode" });

//setup tokenprovider
monaco.languages.setMonarchTokensProvider("redcode", {
	// Define tokenizer rules
	tokenizer: {
		root: [
			// Comments
			[/(;.*$)/, "comment"],

			// Labels
			[/^[a-zA-Z_]\w*:/, "type.identifier"],

			// Instructions (keywords)
			[keywordListRegex, "keyword"],

			// Numbers
			[/\b\d+\b/, "number"],

			// Operators (e.g., addressing modes)
			[/[#$@<>]/, "operator"],

			// Any other symbol
			[/[.,]/, "delimiter"],
		],
	},
});

monaco.languages.registerCompletionItemProvider("redcode", {
	provideCompletionItems: (_, position) => {
		const model = monaco.editor.getModels()[0];
		// Get the word until the current position to find the appropriate range
		const wordInfo = model.getWordUntilPosition(position);

		// Define the range for the completion item
		const range = new monaco.Range(
			position.lineNumber,
			wordInfo.startColumn,
			position.lineNumber,
			wordInfo.endColumn,
		);
		const suggestions = upperkKeywordsList.map((keyword) => ({
			label: keyword.toUpperCase(),
			kind: monaco.languages.CompletionItemKind.Keyword,
			insertText: keyword.toUpperCase(),
			range: range,
		}));
		return { suggestions };
	},
});

monaco.editor.defineTheme("corewarTheme", {
	base: "vs-dark",
	inherit: true,
	rules: [
		{ token: "comment", foreground: "#a67a4b", fontStyle: "italic" },
		{ token: "keyword", foreground: "#53d62f" },
		{ token: "number", foreground: "#bf7a98" },
	],
	colors: {
		"editor.background": "#000000",
	},
});
monaco.editor.setTheme("customTheme");

loader.config({ monaco });

loader.init();
