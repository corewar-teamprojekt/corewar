import { Lobby } from "@/domain/Lobby";
import { LobbyStatus } from "@/domain/LobbyStatus";

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

export async function createLobby(): Promise<number> {
	return await fetch(`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby`, {
		method: "POST",
	}).then((response) => response.json().then((data) => data.lobbyId as number));
}

export async function getLobbyStatus(lobbyId: number): Promise<LobbyStatus> {
	return await fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby/` +
			lobbyId +
			"/status",
	).then((response) => response.json().then((data) => data as LobbyStatus));
}

export async function joinLobby(
	playerName: string,
	lobbyId: number,
): Promise<Response> {
	return await fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby/` +
			lobbyId +
			"/join",
		{
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				playerName: playerName,
			}),
		},
	);
}
