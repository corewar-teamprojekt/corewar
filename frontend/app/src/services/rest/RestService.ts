// later when more api endpoints are added, this can be split into multiple files

import { Linterlint } from "@/domain/LinterLint";
import { Lobby } from "@/domain/Lobby";

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

export async function getLinterLintsV1(code: string): Promise<Linterlint[]> {
	const response = await fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/redcode/compile/errors`,
		{
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				code: code,
			}),
		},
	);
	if (response.ok) {
		const lints = await response
			.json()
			.then((data) => data.errors as Linterlint[]);
		return lints;
	} else {
		return [];
	}
}

export async function getLobbiesV1(): Promise<Lobby[]> {
	const response = await fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby`,
	);
	if (response.ok) {
		const lobbies = await response
			.json()
			.then((data) => data.lobbies as Lobby[]);
		return lobbies;
	} else {
		return [];
	}
}
