import { Lobby } from "@/domain/Lobby.ts";
import { useContext } from "react";
import {
	LobbyContext,
	LobbyDispatchContext,
} from "@/services/lobbyContext/LobbyContext.tsx";

export function useLobby() {
	return useContext(LobbyContext);
}

export function useDispatchLobby() {
	return useContext(LobbyDispatchContext);
}

export function lobbyReducer(
	_lobby: Lobby | null,
	action: { type: string; lobby: Lobby | null },
) {
	switch (action.type) {
		case "join": {
			return action.lobby;
		}
		case "leave": {
			return null;
		}
		default: {
			throw Error("Unknown action: " + action.type);
		}
	}
}
