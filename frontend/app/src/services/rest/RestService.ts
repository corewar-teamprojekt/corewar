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

//eslint is disabled because this function is not implemented yet
// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function getLinterLintsV1(code: string): Promise<Linterlint[]> {
	return Promise.resolve([]);
}
