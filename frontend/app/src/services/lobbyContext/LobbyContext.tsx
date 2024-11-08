import {
	Context,
	createContext,
	Dispatch,
	ReactNode,
	Reducer,
	useReducer,
} from "react";
import { Lobby } from "@/domain/Lobby.ts";
import { lobbyReducer } from "@/services/lobbyContext/LobbyContextHelpers.ts";

export const LobbyContext: Context<Lobby | null> = createContext<Lobby | null>(
	null,
);
export const LobbyDispatchContext: Context<Dispatch<{
	type: string;
	lobby: Lobby | null;
}> | null> = createContext<Dispatch<{
	type: string;
	lobby: Lobby | null;
}> | null>(null);

export function LobbyProvider({ children }: { children: ReactNode }) {
	const [lobby, dispatch] = useReducer<
		Reducer<Lobby | null, { type: string; lobby: Lobby | null }>
	>(lobbyReducer, null);

	return (
		<LobbyContext.Provider value={lobby}>
			<LobbyDispatchContext.Provider value={dispatch}>
				{children}
			</LobbyDispatchContext.Provider>
		</LobbyContext.Provider>
	);
}
