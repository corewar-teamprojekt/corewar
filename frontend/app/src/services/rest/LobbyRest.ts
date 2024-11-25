import { Lobby } from "@/domain/Lobby";
import { LobbyStatus } from "@/domain/LobbyStatus";
import { GameState } from "@/domain/GameState.ts";

interface LobbyDTO {
	lobbyId: number;
	playersJoined: string[];
	gameState: GameState;
}

export async function getLobbiesV1(): Promise<Lobby[]> {
	const response = await fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby`,
	);
	if (response.ok) {
		const data = await response.json();
		return data.lobbies.map(
			(lobby: LobbyDTO) =>
				new Lobby(lobby.lobbyId, lobby.playersJoined, lobby.gameState),
		);
	} else {
		return [];
	}
}

export async function createLobbyV1(playerName: string): Promise<number> {
	return await fetch(`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			playerName: playerName,
		}),
	}).then((response) => response.json().then((data) => data.lobbyId as number));
}

export async function getLobbyStatusV1(lobbyId: number): Promise<LobbyStatus> {
	return await fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby/` +
			lobbyId +
			"/status",
	).then((response) => response.json().then((data) => data as LobbyStatus));
}

export async function joinLobbyV1(
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
