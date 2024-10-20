import LoadingSpinner from "@/components/loadingSpinner/LoadingSpinner.tsx";
import styles from "./WaitingForResult.module.css";
import { useEffect, useRef, useState } from "react";
import { usePageVisibility } from "@/lib/usePageVisibility.ts";
import { useNavigate } from "react-router-dom";
import { POLLING_INTERVAL_MS } from "@/pages/waitingForResult/consts.ts";
import { StatusResponse } from "@/domain/StatusResponse.ts";
import { GameState } from "@/domain/GameState.ts";
import { BACKEND_BASE_URL } from "@/domain/consts.ts";

function WaitingForResultPage() {
	const isPageVisible = usePageVisibility();
	const timerIdRef = useRef<ReturnType<typeof setInterval> | null>(null);
	const [isPollingEnabled, setIsPollingEnabled] = useState(true);
	const navigate = useNavigate();

	useEffect(() => {
		const pollingCallback = async () => {
			console.debug("Polling game status...");

			const response = await fetch(BACKEND_BASE_URL + "/status");
			const data: StatusResponse = await response.json();

			if (data.gameState === GameState.FINISHED) {
				setIsPollingEnabled(false);
				console.log("Game finished. Stopped polling. Rerouting");
				navigate("/result-display");
			}
		};

		const startPolling = () => {
			timerIdRef.current = setInterval(pollingCallback, POLLING_INTERVAL_MS);
		};

		const stopPolling = () => {
			if (timerIdRef.current !== null) {
				clearInterval(timerIdRef.current);
				timerIdRef.current = null;
			}
		};

		if (isPageVisible && isPollingEnabled) {
			startPolling();
		} else {
			stopPolling();
		}

		return () => {
			stopPolling();
		};
	}, [isPageVisible, isPollingEnabled, navigate]);

	return (
		<>
			<div id={styles["waitingForResultHeadline"]}>
				<h2 className="text-3xl font-semibold">Waiting for game result...</h2>
			</div>
			<div id={styles["loadingSpinnerContainer"]}>
				<LoadingSpinner />
			</div>
		</>
	);
}

export default WaitingForResultPage;
