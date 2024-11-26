import bluePlayerIcon from "@/assets/bluePlayerIcon.svg";
import icon from "@/assets/icon.svg";
import redPlayerIcon from "@/assets/redPlayerIcon.svg";
import { Button } from "@/components/ui/button";
import { LobbyStatus } from "@/domain/LobbyStatus";
import { useLobby } from "@/services/lobbyContext/LobbyContextHelpers";
import { getLobbyStatusV1 } from "@/services/rest/LobbyRest";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function ResultDisplayPageV2() {
	const [lobbyStatus, setLobbyStatus] = useState<LobbyStatus>();
	const navigate = useNavigate();
	const lobby = useLobby();
	const user = useUser();

	useEffect(() => {
		if (lobby) {
			getLobbyStatusV1(lobby.id).then((status) => setLobbyStatus(status));
		}
	}, [lobby]);

	function getResultText(): string {
		if (!lobby || !user || !lobbyStatus) {
			return "Error";
		}
		if (lobbyStatus?.result.winner === "DRAW") {
			return "It's a draw!";
		} else if (user?.name.slice(-1) === lobbyStatus?.result.winner) {
			return "You won!";
		} else {
			return "You lost!";
		}
	}

	function getIconForWinner() {
		if (
			lobbyStatus?.result.winner === "DRAW" ||
			!lobby ||
			!user ||
			!lobbyStatus
		) {
			return icon;
		} else if (lobbyStatus?.result.winner === "A") {
			return redPlayerIcon;
		} else {
			return bluePlayerIcon;
		}
	}

	function getAllPlayerVSString(): string {
		if (!lobby || !lobbyStatus || lobby.playersJoined.length < 2) {
			return "";
		}
		let str = lobby.playersJoined[0];
		for (let i = 1; i < lobby.playersJoined.length; i++) {
			str += " vs " + lobby.playersJoined[i];
		}
		return str;
	}

	return (
		<div>
			<div className="flex flex-col justify-center items-center h-[100%] w-[100%] gap-10">
				<h2 className="text-3xl font-semibold">
					Result: {getAllPlayerVSString()}
				</h2>
				<div className="flex flex-row items-center gap-[110px] justify-center">
					<h2 className="text-7xl font-extrabold mb-[110px]">
						{getResultText()}
					</h2>
					<img
						src={getIconForWinner()}
						alt="winner player icon"
						className={"w-164 h-164"}
					/>
				</div>
				<Button onClick={() => navigate("/player-selection")}>
					Play again
				</Button>
			</div>
		</div>
	);
}
