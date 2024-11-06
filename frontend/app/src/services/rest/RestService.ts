// later when more api endpoints are added, this can be split into multiple files

import { Linterlint } from "@/domain/LinterLint";

export function uploadPlayerCode(
	playerName: string,
	code: string,
): Promise<Response> {
	return fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v0/code/${playerName}`,
		{
			method: "POST",
			body: code,
		},
	);
}

export function getStatusV0(): Promise<Response> {
	return fetch(`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v0/status`);
}

export function getLinterLintsV1(code: string): Promise<Linterlint[]> {
	console.debug("fetching lints for code:, ", code);
	return Promise.resolve([]);
}
