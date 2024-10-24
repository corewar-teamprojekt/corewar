import { useToast } from "@/hooks/use-toast";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { ReactNode, useEffect } from "react";
import { useNavigate } from "react-router-dom";

interface RequireUserProps {
	children: ReactNode;
}

export function RequireUser({ children }: Readonly<RequireUserProps>) {
	const user = useUser();
	const { toast } = useToast();
	const navigate = useNavigate();

	useEffect(handleAuthorizationAndReroute, [navigate, toast, user]);

	function handleAuthorizationAndReroute() {
		if (!user) {
			toast({
				title: "Oopsie👉👈",
				description: "You have to be logged in to be here 😢",
				variant: "destructive",
			});
			navigate("/player-selection");
		}
	}

	return <>{children}</>;
}
