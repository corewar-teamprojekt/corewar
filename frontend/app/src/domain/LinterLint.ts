import { MarkerSeverity } from "monaco-editor/esm/vs/editor/editor.api";

export interface Linterlint {
	severity: MarkerSeverity;
	startLineNumber: number;
	endLineNumber: number;
	startColumn: number;
	endColumn: number;
	message: string;
}
