import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { createBrowserRouter, Link, RouterProvider } from "react-router-dom";
import ErrorPage from "@/pages/ErrorPage.tsx";

const router = createBrowserRouter([
	{
		path: "/",
		element: <App></App>,
		errorElement: <ErrorPage />,
	},
	{
		path: "/demo-route",
		element: <Link to={"/"}>route back</Link>,
	},
]);

createRoot(document.getElementById("root")!).render(
	<StrictMode>
		<RouterProvider router={router} />
	</StrictMode>,
);
