import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import ErrorPage from "@/pages/ErrorPage.tsx";
import { UserProvider } from "@/services/userContext/UserContext.tsx";
import { ThemeProvider } from "@/components/ui/ThemeProvider.tsx";
import WaitingForResultPage from "@/pages/waitingForResult/WaitingForResultPage.tsx";
import { Toaster } from "./components/ui/toaster.tsx";
import PlayerCodingPage from "./pages/playerCodeInput/PlayerCodingPage.tsx";
import LandingPage from "@/pages/landingPage/LandingPage.tsx";
import PlayerSelection from "@/pages/playerSelection/PlayerSelection.tsx";
import BasePage from "@/pages/basePage/BasePage.tsx";

const router = createBrowserRouter([
	{
		path: "/",
		element: <BasePage />,
		errorElement: <ErrorPage />,
		children: [
			{
				index: true,
				element: <LandingPage enableThreeJs={true} />,
			},
			{
				path: "waiting-for-result",
				element: <WaitingForResultPage />,
			},
			{
				path: "player-coding",
				element: <PlayerCodingPage />,
			},
			{
				path: "/player-selection",
				element: <PlayerSelection />,
			},
		],
	},
]);

createRoot(document.getElementById("root")!).render(
	<StrictMode>
		<UserProvider>
			<ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
				<RouterProvider router={router} />
				<Toaster />
			</ThemeProvider>
		</UserProvider>
	</StrictMode>,
);
