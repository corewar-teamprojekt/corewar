import { Lobby } from "@/domain/Lobby.ts";
import { GameState } from "@/domain/GameState.ts";

export function aLobby(): Lobby {
	return new Lobby(0, ["playerA"], GameState.NOT_STARTED);
}

export function anotherLobby(): Lobby {
	return new Lobby(1, ["playerB"], GameState.NOT_STARTED);
}
