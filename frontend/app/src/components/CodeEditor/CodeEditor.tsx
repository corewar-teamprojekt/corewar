import { BASE_POLLING_INTERVAL_MS } from "@/consts";
import { Linterlint } from "@/domain/LinterLint";
import { usePageVisibility } from "@/lib/usePageVisibility";
import { getLinterLintsV1 } from "@/services/rest/RestService";
import { Editor, useMonaco } from "@monaco-editor/react";
import { useEffect, useRef } from "react";

interface CodeEditorProps {
	setProgram: (s: string) => void;
}

export default function CodeEditor({ setProgram }: Readonly<CodeEditorProps>) {
	const monaco = useMonaco();
	const linterOwner = "redCodeLinter";
	const isPageVisible = usePageVisibility();
	const timerIdRef = useRef<ReturnType<typeof setInterval> | null>(null);

	useEffect(() => {
		const pollingCallback = async () => {
			console.debug("Polling linter warnings...");

			const responseLints = await getLinterLintsV1(
				monaco?.editor.getModels()[0].getValue() ?? "",
			);
			setLinterLinting(responseLints);
		};

		const startPolling = () => {
			timerIdRef.current = setInterval(
				pollingCallback,
				BASE_POLLING_INTERVAL_MS,
			);
		};

		const stopPolling = () => {
			if (timerIdRef.current !== null) {
				clearInterval(timerIdRef.current);
				timerIdRef.current = null;
			}
		};

		if (isPageVisible) {
			startPolling();
		} else {
			stopPolling();
		}

		return () => {
			stopPolling();
		};
	}, [isPageVisible, monaco]);

	function setLinterLinting(linterLints: Linterlint[]) {
		if (!monaco) return;
		const model = monaco.editor.getModels()[0];
		const markers = [];
		for (const ll of linterLints) {
			const marker = {
				severity: ll.severity,
				startLineNumber: ll.startLineNumber,
				startColumn: ll.startColumn,
				endLineNumber: ll.endLineNumber,
				endColumn: ll.endColumn,
				message: ll.message,
				owner: linterOwner,
				resource: model.uri,
			};
			markers.push(marker);
		}
		monaco.editor.setModelMarkers(model, linterOwner, markers);
	}

	return (
		<Editor
			height="60vh"
			theme="corewarTheme"
			defaultLanguage="redcode"
			onMount={(editor) => setProgram(editor.getValue())}
			onChange={(value) => setProgram(value ?? "")}
		/>
	);
}
