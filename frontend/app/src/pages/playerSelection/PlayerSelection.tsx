import { Button } from "@/components/ui/button.tsx";
import { useNavigate } from "react-router-dom";
import { useDispatchUser } from "@/services/userContext/UserContextHelpers.ts";

function PlayerSelection() {
	const navigate = useNavigate();
	const dispatcher = useDispatchUser();

	return (
		<Button
			onClick={() => {
				if (dispatcher) {
					dispatcher({
						type: "setPlayerA",
						user: null,
					});
				}
				navigate("/player-coding");
			}}
		>
			Route
		</Button>
	);
}

export default PlayerSelection;
