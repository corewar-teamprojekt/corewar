import LoadingSpinner from "@/components/loadingSpinner/LoadingSpinner.tsx";
import { RequireUser } from "@/components/requireUser.tsx/RequireUser";
import { GameState } from "@/domain/GameState.ts";
import { StatusResponse } from "@/domain/StatusResponse.ts";
import { BASE_POLLING_INTERVAL_MS } from "@/consts.ts";
import { usePageVisibility } from "@/lib/usePageVisibility.ts";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./WaitingForOpponent.module.css";
import { getStatusV0 } from "@/services/rest/RestService.ts";

function WaitingForOpponentPage() {
	const isPageVisible = usePageVisibility();
	const timerIdRef = useRef<ReturnType<typeof setInterval> | null>(null);
	const [isPollingEnabled, setIsPollingEnabled] = useState(true);
	const navigate = useNavigate();

	useEffect(() => {
		const pollingCallback = async () => {
			console.debug("Polling game status...");

			const response = await getStatusV0();

			if (response.status >= 500) {
				return;
			}

			const data: StatusResponse = await response.json();

			if (data.gameState != GameState.NOT_STARTED) {
				setIsPollingEnabled(false);
				console.log("Game started. Stopped polling. Rerouting");
				navigate("/waiting-for-result");
			}
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
		<RequireUser>
			<div id={styles["waitingForOpponentHeadline"]}>
				<h2 className="text-3xl font-semibold">Waiting for opponent...</h2>
			</div>
			<div id={styles["loadingSpinnerContainer"]}>
				<LoadingSpinner />
			</div>
		</RequireUser>
	);
}

export default WaitingForOpponentPage;
