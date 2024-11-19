import bluePlayerIcon from "@/assets/bluePlayerIcon.svg";
import redPlayerIcon from "@/assets/redPlayerIcon.svg";
import { RequireLogout } from "@/components/requireLogout/RequireLogout";
import { GameState } from "@/domain/GameState";
import { Lobby } from "@/domain/Lobby";
import { useToast } from "@/hooks/use-toast";
import {
	useDispatchLobby,
	useLobby,
} from "@/services/lobbyContext/LobbyContextHelpers";
import { createLobby, joinLobby } from "@/services/rest/LobbyRest";
import {
	useDispatchUser,
	useUser,
} from "@/services/userContext/UserContextHelpers.ts";
import { useEffect, useState } from "react";
import { redirect, useNavigate } from "react-router-dom";

function PlayerSelection() {
	const navigate = useNavigate();
	const userDispatcher = useDispatchUser();
	const lobby = useLobby();
	const user = useUser();
	const lobbyDispatch = useDispatchLobby();
	const [blockLogout, setBlockLogout] = useState<boolean>(false);
	const [shouldRedirect, setShouldRedirect] = useState(false);
	const { toast } = useToast();

	useEffect(() => {
		if (lobby && user && shouldRedirect) {
			navigate("/player-coding");
			setBlockLogout(false);
		}
	}, [lobby, user, shouldRedirect, navigate]);

	useEffect(() => {
		function createAndJoinNewLobby() {
			if (!user || !lobbyDispatch) {
				return;
			}
			createLobby().then((lobbyId) => {
				joinLobby(user.name, lobbyId)
					.then(() => {
						const newLobby = new Lobby(
							lobbyId,
							[user.name],
							GameState.NOT_STARTED,
						);
						lobbyDispatch({ type: "join", lobby: newLobby });
						setShouldRedirect(true);
					})
					.catch(() => {
						toast({
							title: "Oopsie👉👈",
							description: "Something went wrong while joining the lobby 😢",
							variant: "destructive",
						});
						redirect("lobby-selection");
					});
			});
		}

		if (!user || !lobbyDispatch) {
			return;
		} else if (lobby) {
			joinLobby(user.name, lobby.id).then(() => {
				setShouldRedirect(true);
			});
		} else {
			createAndJoinNewLobby();
		}
	}, [user, lobby, lobbyDispatch, toast]);

	function setPlayer(actionType: "setPlayerA" | "setPlayerB") {
		if (userDispatcher) {
			setBlockLogout(true);
			userDispatcher({
				type: actionType,
				user: null,
			});
		}
	}

	return (
		<RequireLogout blocked={blockLogout}>
			<div className="h-[100%] w-[100%] flex flex-col justify-center items-center ">
				<div className="h-[100%] w-[100%] flex flex-row justify-center items-center gap-[12%]">
					<button onClick={() => setPlayer("setPlayerA")}>
						<img
							src={redPlayerIcon}
							alt="Player A Icon"
							className="w-164 h-164 transition-transform duration-300 hover:scale-110"
						/>
					</button>
					<button onClick={() => setPlayer("setPlayerB")}>
						<img
							src={bluePlayerIcon}
							alt="Player B Icon"
							className="w-164 h-164 transition-transform duration-300 hover:scale-110"
						/>
					</button>
				</div>
				<h1 className="text-3xl font-extrabold mb-20">Select a color</h1>
			</div>
		</RequireLogout>
	);
}

export default PlayerSelection;
