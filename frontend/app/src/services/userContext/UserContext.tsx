import { Context, createContext, Dispatch, ReactNode, useReducer } from "react";
import { User } from "@/domain/user.ts";
import {
	playerA,
	userReducer,
} from "@/services/userContext/UserContextHelpers.ts";

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
