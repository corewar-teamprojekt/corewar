// later when more api endpoints are added, this can be split into multiple files

import { GameState } from "@/domain/GameState";
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

export async function getLobbbiesV1(): Promise<Lobby[]> {
	const mockLobbies: Lobby[] = [
		{
			id: 1,
			playersJoined: ["Alice"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 2,
			playersJoined: ["Charlie", "Dave"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 3,
			playersJoined: ["Frank"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 4,
			playersJoined: ["Grace"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 5,
			playersJoined: ["Ivan", "Judy"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 6,
			playersJoined: ["Kevin"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 7,
			playersJoined: ["Laura"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 8,
			playersJoined: ["Mike"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 9,
			playersJoined: ["Nina"],
			gameState: GameState.NOT_STARTED,
		},
		{
			id: 10,
			playersJoined: ["Oscar"],
			gameState: GameState.NOT_STARTED,
		},
	];
	return Promise.resolve(mockLobbies);
	// const response = await fetch(`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby`);
	// if (response.ok) {
	// 	const lobbies = await response.json().then((data) => data.lobbies as Lobby[]);
	// 	return lobbies;
	// } else {
	// 	return [];
	// }
}
