import {
	Context,
	createContext,
	Dispatch,
	ReactNode,
	useContext,
	useReducer,
} from "react";
import { User } from "@/domain/user.ts";

export const UserContext: Context<User | null> = createContext<User | null>(
	null,
);
export const UserDispatchContext: Context<Dispatch<{
	type: string;
	user: User | null;
}> | null> = createContext<Dispatch<{
	type: string;
	user: User | null;
}> | null>(null);

export function UserProvider({ children }: { children: ReactNode }) {
	const [user, dispatch] = useReducer(userReducer, playerA);

	return (
		<UserContext.Provider value={user}>
			<UserDispatchContext.Provider value={dispatch}>
				{children}
			</UserDispatchContext.Provider>
		</UserContext.Provider>
	);
}
// See https://react.dev/learn/scaling-up-with-reducer-and-context, they suggest this there
// eslint-disable-next-line react-refresh/only-export-components
export function useUser() {
	return useContext(UserContext);
}

// See https://react.dev/learn/scaling-up-with-reducer-and-context, they suggest this there
// eslint-disable-next-line react-refresh/only-export-components
export function useDispatchUser() {
	return useContext(UserDispatchContext);
}

// the user field in action is currently nullable and pretty much everywhere null, but I think we need it for the reducer,
// and since we will need it for the future, we are just gonna keep it :3
// Export for testing
// eslint-disable-next-line react-refresh/only-export-components
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

const playerA: User = new User("PlayerA", "#FF0000");
const playerB: User = new User("PlayerB", "#0000FF");
