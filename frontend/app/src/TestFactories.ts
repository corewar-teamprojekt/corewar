import { Lobby } from "@/domain/Lobby.ts";
import { GameState } from "@/domain/GameState.ts";
import { LobbyStatus } from "./domain/LobbyStatus";

interface NamedLobbyParameters {
	lobbyId?: number;
	playersJoined?: string[];
	gameState?: GameState;
}

export function aLobby({
	lobbyId = 0,
	playersJoined = ["playerA"],
	gameState = GameState.NOT_STARTED,
}: NamedLobbyParameters = {}): Lobby {
	return new Lobby(lobbyId, playersJoined, gameState);
}

export function anotherLobby(): Lobby {
	return new Lobby(1, ["playerB"], GameState.NOT_STARTED);
}

export function mockLobbies(): Lobby[] {
	return [
		new Lobby(1, ["Alice"], GameState.NOT_STARTED),
		new Lobby(2, ["Charlie", "Dave"], GameState.NOT_STARTED),
		new Lobby(3, ["Frank"], GameState.NOT_STARTED),
		new Lobby(4, ["Grace"], GameState.NOT_STARTED),
		new Lobby(5, ["Ivan", "Judy"], GameState.NOT_STARTED),
		new Lobby(6, ["Kevin"], GameState.NOT_STARTED),
		new Lobby(7, ["Laura"], GameState.NOT_STARTED),
		new Lobby(8, ["Mike"], GameState.NOT_STARTED),
		new Lobby(9, ["Nina"], GameState.NOT_STARTED),
		new Lobby(10, ["Oscar"], GameState.NOT_STARTED),
	];
}

export const mockResultWinnerA: LobbyStatus = {
	playerASubmitted: true,
	playerBSubmitted: true,
	gameState: GameState.FINISHED,
	result: {
		winner: "A",
	},
	visualizationData: [],
};
export const mockResultWinnerB: LobbyStatus = {
	playerASubmitted: true,
	playerBSubmitted: true,
	gameState: GameState.FINISHED,
	result: {
		winner: "B",
	},
	visualizationData: [],
};
export const mockResultDraw: LobbyStatus = {
	playerASubmitted: true,
	playerBSubmitted: true,
	gameState: GameState.FINISHED,
	result: {
		winner: "DRAW",
	},
	visualizationData: [],
};
