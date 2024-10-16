import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { createBrowserRouter, Link, RouterProvider } from "react-router-dom";
import ErrorPage from "@/pages/ErrorPage.tsx";
import BasePage from "@/pages/basePage/BasePage.tsx";
import { UserProvider } from "@/services/userContext/UserContext.tsx";
import { ThemeProvider } from "@/components/ui/ThemeProvider.tsx";
import WaitingForResultPage from "@/pages/waitingForResult/WaitingForResultPage.tsx";

const router = createBrowserRouter([
	{
		path: "/",
		element: <App />,
		errorElement: <ErrorPage />,
	},
	{
		path: "/demo-route",
		element: (
			<BasePage>
				<Link to={"/"}>route back</Link>
			</BasePage>
		),
	},
	{
		path: "/waiting-for-result",
		element: <WaitingForResultPage />,
	},
]);

createRoot(document.getElementById("root")!).render(
	<StrictMode>
		<UserProvider>
			<ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
				<RouterProvider router={router} />
			</ThemeProvider>
		</UserProvider>
	</StrictMode>,
);
