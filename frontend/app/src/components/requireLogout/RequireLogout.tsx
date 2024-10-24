import {
	useDispatchUser,
	useUser,
} from "@/services/userContext/UserContextHelpers";
import { ReactNode, useEffect } from "react";

interface RequireUserProps {
	blocked: boolean;
	children: ReactNode;
}

export function RequireLogout({
	blocked,
	children,
}: Readonly<RequireUserProps>) {
	const user = useUser();
	const dispatcher = useDispatchUser();

	useEffect(handleUserChanges, [blocked, dispatcher, user]);

	function handleUserChanges() {
		if (user && dispatcher && !blocked) {
			dispatcher({
				type: "logout",
				user: null,
			});
		}
	}

	return <>{children}</>;
}
