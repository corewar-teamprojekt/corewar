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
import { useNavigate } from "react-router-dom";
import styles from "./PlayerSelection.module.css";

function PlayerSelection() {
	const navigate = useNavigate();

	const userDispatcher = useDispatchUser();
	const lobbyDispatch = useDispatchLobby();

	const lobby = useLobby();
	const user = useUser();

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
		async function joinLobbyWithPlayer(userName: string, lobbyId: number) {
			if (!lobbyDispatch) {
				return;
			}
			joinLobby(userName, lobbyId)
				.catch(handleLobbyError)
				.then(() => {
					if (!lobby || lobby.id !== lobbyId) {
						const newLobby = new Lobby(
							lobbyId,
							[userName],
							GameState.NOT_STARTED,
						);
						lobbyDispatch({ type: "join", lobby: newLobby });
					}
					setShouldRedirect(true);
				});
		}

		function handleLobbyError() {
			toast({
				title: "Oopsie👉👈",
				description: "Something went wrong while joining the lobby 😢",
				variant: "destructive",
			});
			if (userDispatcher) userDispatcher({ type: "logout", user: null });
			navigate("/lobby-selection");
			setBlockLogout(false);
		}

		if (!user || !lobbyDispatch) {
			return;
		} else if (lobby) {
			joinLobbyWithPlayer(user.name, lobby.id);
		} else {
			createLobby()
				.catch(handleLobbyError)
				.then((lobbyID) => {
					if (lobbyID) joinLobbyWithPlayer(user.name, lobbyID);
				});
		}
	}, [user, lobby, lobbyDispatch, toast, userDispatcher, navigate]);

	function setPlayer(actionType: "setPlayerA" | "setPlayerB") {
		if (userDispatcher) {
			setBlockLogout(true);
			userDispatcher({
				type: actionType,
				user: null,
			});
		}
	}

	function isButtonDisabled(player: string): boolean {
		return lobby?.playersJoined.includes(player) ?? false;
	}

	return (
		<RequireLogout blocked={blockLogout}>
			<div className="h-[100%] w-[100%] flex flex-col justify-center items-center ">
				<div className="h-[100%] w-[100%] flex flex-row justify-center items-center gap-[12%]">
					<button
						onClick={() => setPlayer("setPlayerA")}
						disabled={isButtonDisabled("playerA")}
					>
						<img
							src={redPlayerIcon}
							alt="Player A Icon"
							className={
								styles[
									isButtonDisabled("playerA")
										? "player-icon-disabled"
										: "player-icon"
								]
							}
						/>
					</button>
					<button
						onClick={() => setPlayer("setPlayerB")}
						disabled={isButtonDisabled("playerB")}
					>
						<img
							src={bluePlayerIcon}
							alt="Player B Icon"
							className={
								styles[
									isButtonDisabled("playerB")
										? "player-icon-disabled"
										: "player-icon"
								]
							}
						/>
					</button>
				</div>
				<h1 className="text-3xl font-extrabold mb-20">Select a color</h1>
			</div>
		</RequireLogout>
	);
}

export default PlayerSelection;
