import LobbySelection from "@/components/LobbySelection/LobbySelection";
import { RequireLogout } from "@/components/requireLogout/RequireLogout";
import { Button } from "@/components/ui/button";
import { BASE_POLLING_INTERVAL_MS } from "@/consts";
import { Lobby } from "@/domain/Lobby";
import { usePageVisibility } from "@/lib/usePageVisibility";
import { useDispatchLobby } from "@/services/lobbyContext/LobbyContextHelpers";
import { getLobbiesV1 } from "@/services/rest/LobbyRest";
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
			const fetchedLobbies = await getLobbiesV1();
			const mergedLobbies = mergeLobbies(fetchedLobbies);
			setLobbies(mergedLobbies);
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
		//eslint wants to create an endless loop again by adding a function to the dependency array :(
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [isPageVisible, lobbies]);

	function joinLobby(lobby: Lobby) {
		if (lobbyDispatch) {
			lobbyDispatch({ lobby: lobby, type: "join" });
			navigate("/player-selection");
		}
	}

	/**
	 * 1. new free lobby with new id
	 * 	1.1: when there is a full / disabled lobby, the new lobby should replace that one
	 * 	1.2: there is no full / disabled lobby, the new lobby should be added at the end
	 * 2. existing lobbyID: replaces the existing lobby
	 * 3. lobby is missing: the existing lobby is disabled
	 * 4. new full lobby with new id: should not be added
	 */
	function mergeLobbies(newLobbies: Lobby[]): Lobby[] {
		const newFreeLobbies = filterFullLobbies(newLobbies);
		newFreeLobbies.forEach((lobby) => (lobby.isDisabled = false));

		const mergedLobbies: Lobby[] = lobbies.map(
			(lobby) => new Lobby(lobby.id, lobby.playersJoined, lobby.gameState),
		);
		const newlyAddedLobbies: Lobby[] = [];
		mergedLobbies.forEach((lobby) => (lobby.isDisabled = true));

		//refresh existing lobbies and add new lobbies to newlyAddedLobbies
		for (const newLobby of newFreeLobbies) {
			const existingLobby = mergedLobbies.find(
				(lobby) => lobby.id === newLobby.id,
			);
			if (existingLobby) {
				Object.assign(existingLobby, newLobby);
			} else {
				newlyAddedLobbies.push(newLobby);
			}
		}

		//replace deleted or full lobbies with new lobbies or add new lobbies at the end
		for (const newlyAddedLobby of newlyAddedLobbies) {
			const fullOrDisabledLobby = mergedLobbies.find(
				(lobby) => lobby.isDisabled || lobby.isLobbyFull(),
			);
			if (fullOrDisabledLobby) {
				Object.assign(fullOrDisabledLobby, newlyAddedLobby);
			} else {
				mergedLobbies.push(newlyAddedLobby);
			}
		}

		return mergedLobbies;
	}

	function filterFullLobbies(lobbies: Lobby[]): Lobby[] {
		return lobbies.filter((lobby) => !lobby.isLobbyFull());
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
