import { RequireLogout } from "@/components/requireLogout/RequireLogout";
import { Button } from "@/components/ui/button.tsx";
import { useDispatchUser } from "@/services/userContext/UserContextHelpers.ts";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

function PlayerSelection() {
	const navigate = useNavigate();
	const dispatcher = useDispatchUser();
	const [blockLogout, setBlockLogout] = useState<boolean>(false);

	function setPlayerAndRedirectToCoding(
		actionType: "setPlayerA" | "setPlayerB",
	) {
		if (dispatcher) {
			setBlockLogout(true);
			dispatcher({
				type: actionType,
				user: null,
			});
		}
		navigate("/player-coding");
		setBlockLogout(false);
	}

	return (
		<RequireLogout blocked={blockLogout}>
			<div className="h-[100%] w-[100%] flex flex-row  justify-center items-center gap-[12%] ">
				<div className="h-[30%] flex flex-col gap-y-5 justify-start items-center">
					<h1 className="text-red-500 text-8xl font-extrabold">PLAYER A</h1>
					<Button
						className="max-w-[30%] min-w-[20%]"
						onClick={() => setPlayerAndRedirectToCoding("setPlayerA")}
					>
						PLAY
					</Button>
				</div>
				<div className="h-[30%] flex flex-col gap-y-5 justify-start items-center">
					<h1 className="text-blue-500 text-8xl font-extrabold">PLAYER B</h1>
					<Button
						className="max-w-[30%] min-w-[20%]"
						onClick={() => setPlayerAndRedirectToCoding("setPlayerB")}
					>
						PLAY
					</Button>
				</div>
			</div>
		</RequireLogout>
	);
}

export default PlayerSelection;
