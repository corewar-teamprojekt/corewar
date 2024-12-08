import HexagonalBoard from "@/components/hexagonalBoard/HexagonalBoard.tsx";
import "./gameVisu.css";
import Header from "@/components/header/Header.tsx";
import Footer from "@/components/footer/Footer.tsx";
import { useEffect, useRef, useState } from "react";
import { HexagonalTileProps } from "@/components/hexagonalTile/HexagonalTile.tsx";
import { LobbyStatus } from "@/domain/LobbyStatus.ts";
import { useLobby } from "@/services/lobbyContext/LobbyContextHelpers.ts";
import { getLobbyStatusV1 } from "@/services/rest/LobbyRest.ts";
import { useNavigate } from "react-router-dom";

function GameVisuPage() {
	const delay = (ms: number | undefined) =>
		new Promise((res) => setTimeout(res, ms));

	const [lobbyStatus, setLobbyStatus] = useState<LobbyStatus>();
	const lobby = useLobby();
	const navigate = useNavigate();
	const interval_time: number = 33;
	const counterRef = useRef(0);

	useEffect(() => {
		if (lobby) {
			getLobbyStatusV1(lobby.id).then((status) => {
				console.log("setting lobby status");
				setLobbyStatus(status);
				console.log("DONE setting lobby status");
			});
		}
	}, [lobby]);

	useEffect(() => {
		const interval = setInterval(async () => {
			if (lobbyStatus == undefined) {
				console.log("lobby status undefined");
				return;
			}
			if (counterRef.current >= lobbyStatus.visualizationData.length) {
				console.log("Done visualizing!");
				clearInterval(interval);
				await delay(3000);
				navigate("/result-display");
				return;
			}

			if (boardRef.current == null) {
				console.log("board ref undefined");
				return;
			}

			const previousIteration =
				counterRef.current >= 1
					? lobbyStatus.visualizationData[counterRef.current - 1]
					: null;
			const currentIteration =
				lobbyStatus.visualizationData[counterRef.current];

			const playerColor =
				currentIteration.playerId === "playerA" ? "#FF006E" : "#00FFFF";

			// Queue updates instead of applying directly
			for (const writeIndex of currentIteration.memoryWrites) {
				boardRef.current.updateTile(
					Math.floor(writeIndex / 128),
					writeIndex % 128,
					{
						fill: playerColor,
						textContent: "",
						isDimmed: true,
					},
				);
			}

			// Player reads
			for (const readIndex of currentIteration.memoryReads) {
				boardRef.current.updateTile(
					Math.floor(readIndex / 128),
					readIndex % 128,
					{
						textContent: "X",
						textColor: playerColor,
					},
				);
			}

			if (previousIteration != null) {
				// Turn off previous active process
				if (previousIteration.programCounterBefore >= 0) {
					boardRef.current.updateTile(
						Math.floor(previousIteration.programCounterBefore / 128),
						previousIteration.programCounterBefore % 128,
						{
							isDimmed: true,
							stroke: "",
						},
					);
				}

				// Move last active process
				if (previousIteration.programCounterAfter >= 0) {
					boardRef.current.updateTile(
						Math.floor(previousIteration.programCounterAfter / 128),
						previousIteration.programCounterAfter % 128,
						{
							stroke: "#BBBBBB",
							strokeWidth: "16",
						},
					);
				}
			}

			// Active process
			if (currentIteration.programCounterBefore >= 0) {
				boardRef.current.updateTile(
					Math.floor(currentIteration.programCounterBefore / 128),
					currentIteration.programCounterBefore % 128,
					{
						isDimmed: false,
						stroke: "white",
						strokeWidth: "48",
					},
				);
			}

			// Sleeping processes
			for (const sleepingProcess of currentIteration.programCountersOfOtherProcesses) {
				boardRef.current.updateTile(
					Math.floor(sleepingProcess / 128),
					sleepingProcess % 128,
					{
						stroke: "#BBBBBB",
						strokeWidth: "16",
					},
				);
			}

			counterRef.current++;
		}, interval_time);

		// Clear interval on component unmount
		return () => clearInterval(interval);
	}, [lobbyStatus]);

	const boardRef = useRef<{
		updateTile: (
			row: number,
			col: number,
			props: Partial<HexagonalTileProps>,
		) => void;
	}>(null);

	return (
		<div id={"everything"}>
			<Header />
			<div id={"boardBuffer"}>
				<button>{"skip visualization>>"}</button>
			</div>
			<div id={"visuBoard"}>
				<HexagonalBoard
					rows={64}
					tilesPerRow={128}
					ref={boardRef}
				></HexagonalBoard>
			</div>
			<Footer />
		</div>
	);
}

export default GameVisuPage;
