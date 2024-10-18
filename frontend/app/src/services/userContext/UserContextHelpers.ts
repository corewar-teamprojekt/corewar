import { useContext } from "react";
import {
	UserContext,
	UserDispatchContext,
} from "@/services/userContext/UserContext.tsx";
import { User } from "@/domain/user.ts";

export function useUser() {
	return useContext(UserContext);
}

export function useDispatchUser() {
	return useContext(UserDispatchContext);
}

export function userReducer(
	_user: User | null,
	action: { type: string; user: User | null },
) {
	switch (action.type) {
		case "setPlayerA": {
			return playerA;
		}
		case "setPlayerB": {
			return playerB;
		}
		default: {
			throw Error("Unknown action: " + action.type);
		}
	}
}

export const playerA: User = new User("PlayerA", "#FF0000");
export const playerB: User = new User("PlayerB", "#0000FF");
