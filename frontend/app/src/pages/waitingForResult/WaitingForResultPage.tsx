import BasePage from "@/pages/basePage/BasePage.tsx";
import LoadingSpinner from "@/components/loadingSpinner/LoadingSpinner.tsx";
import styles from "./WaitingForResult.module.css";
import { useEffect, useRef, useState } from "react";
import { usePageVisibility } from "@/lib/usePageVisibility.ts";
import { useNavigate } from "react-router-dom";

function WaitingForResultPage() {
	const isPageVisible = usePageVisibility();
	const timerIdRef = useRef<ReturnType<typeof setInterval> | null>(null);
	const [isPollingEnabled, setIsPollingEnabled] = useState(true);
	const navigate = useNavigate();

	useEffect(() => {
		const pollingCallback = async () => {
			// Your polling logic here
			console.log("Polling...");

			const response = await fetch("https://backend/api/status");
			const data = await response.json();

			// TODO: Extract api response and contained enums
			if (data["gameState"] === "FINISHED") {
				setIsPollingEnabled(false);
				console.log("Game finished. Stopped polling. Rerouting");
				navigate("/result-display");
			}
		};

		const startPolling = () => {
			// pollingCallback(); // To immediately start fetching data
			// Polling every 30 seconds
			timerIdRef.current = setInterval(pollingCallback, 1000);
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
		<BasePage>
			<div id={styles["layoutingContainer"]}>
				<div id={styles["waitingForResultHeadline"]}>
					<h2 className="text-3xl font-semibold">Waiting for game result...</h2>
				</div>
				<div id={styles["loadingSpinnerContainer"]}>
					<LoadingSpinner />
				</div>
			</div>
		</BasePage>
	);
}

export default WaitingForResultPage;
