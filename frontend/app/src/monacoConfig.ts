import { loader } from "@monaco-editor/react";

import * as monaco from "monaco-editor";
import editorWorker from "monaco-editor/esm/vs/editor/editor.worker?worker";

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

self.MonacoEnvironment = {
	getWorker() {
		return new editorWorker();
	},
};

monaco.languages.register({ id: "redcode" });

//setup tokenprovider
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
		const suggestions = keywordsList.map((keyword) => ({
			label: keyword,
			kind: monaco.languages.CompletionItemKind.Keyword,
			insertText: keyword,
			range: range,
		}));
		return { suggestions };
	},
});

loader.config({ monaco });

loader.init();
