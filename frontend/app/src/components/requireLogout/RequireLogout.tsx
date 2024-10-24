import {
	useDispatchUser,
	useUser,
} from "@/services/userContext/UserContextHelpers";
import { ReactNode, useEffect } from "react";

interface RequireUserProps {
	children: ReactNode;
}

export function RequireLogout({ children }: Readonly<RequireUserProps>) {
	const user = useUser();
	const dispatcher = useDispatchUser();

	useEffect(handleUserChanges, [dispatcher, user]);

	function handleUserChanges() {
		if (user && dispatcher) {
			dispatcher({
				type: "logout",
				user: null,
			});
		}
	}

	return <>{children}</>;
}
