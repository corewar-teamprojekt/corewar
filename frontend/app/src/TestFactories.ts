import { Lobby } from "@/domain/Lobby.ts";
import { GameState } from "@/domain/GameState.ts";

export function aLobby(): Lobby {
	return new Lobby(0, ["playerA"], GameState.NOT_STARTED);
}

export function anotherLobby(): Lobby {
	return new Lobby(1, ["playerB"], GameState.NOT_STARTED);
}

export function mockLobbies(): Lobby[] {
	return [
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
}
