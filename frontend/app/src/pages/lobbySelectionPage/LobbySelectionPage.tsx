import LobbySelection from "@/components/LobbySelection/LobbySelection";
import { RequireLogout } from "@/components/requireLogout/RequireLogout";
import { Button } from "@/components/ui/button";
import { BASE_POLLING_INTERVAL_MS } from "@/consts";
import { Lobby } from "@/domain/Lobby";
import { usePageVisibility } from "@/lib/usePageVisibility";
import { useDispatchLobby } from "@/services/lobbyContext/LobbyContextHelpers";
import { getLobbiesV1 } from "@/services/rest/RestService";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function LobbySelectionPage() {
	const [lobbies, setLobbies] = useState<Lobby[]>([]);
	const lobbyDispatch = useDispatchLobby();
	const navigate = useNavigate();
	const isPageVisible = usePageVisibility();
	const timerIdRef = useRef<ReturnType<typeof setInterval> | null>(null);

	useEffect(() => {
		const pollingCallback = async () => {
			const lobbies = await getLobbiesV1();
			setLobbies(lobbies);
		};

		const startPolling = () => {
			timerIdRef.current = setInterval(
				pollingCallback,
				BASE_POLLING_INTERVAL_MS,
			);
		};

		const stopPolling = () => {
			if (timerIdRef.current !== null) {
				clearInterval(timerIdRef.current);
				timerIdRef.current = null;
			}
		};

		if (isPageVisible) {
			startPolling();
		} else {
			stopPolling();
		}

		return () => {
			stopPolling();
		};
	}, [isPageVisible]);

	function joinLobby(lobby: Lobby) {
		if (lobbyDispatch) {
			lobbyDispatch({ lobby: lobby, type: "join" });
			navigate("/player-selection");
		}
	}
	return (
		<RequireLogout blocked={false}>
			<div className="h-[100%] w-[100%] flex flex-col justify-center items-center">
				<h1 className="text-2xl font-extrabold mb-[45px]">AVAILABLE LOBBIES</h1>
				<LobbySelection lobbies={lobbies} joinLobby={joinLobby} />
				<Button
					className="mt-[20px]"
					onClick={() => navigate("/player-selection")}
				>
					CREATE LOBBY
				</Button>
			</div>
		</RequireLogout>
	);
}
