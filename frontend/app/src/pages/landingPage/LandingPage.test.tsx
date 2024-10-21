import { it, describe, beforeEach, expect } from "vitest";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import LandingPage from "@/pages/landingPage/LandingPage.tsx";
import { act } from "react";
import PlayerSelection from "@/pages/playerSelection/PlayerSelection.tsx";

const testRouterConfig = [
	{
		path: "/",
		element: <LandingPage enableThreeJs={false}></LandingPage>,
	},
	{
		path: "/player-selection",
		element: <PlayerSelection />,
	},
];
describe("hero button", () => {
	beforeEach(() => {
		cleanup();
	});

	it("routes to player-selection", async () => {
		const router = createMemoryRouter(testRouterConfig);
		act(() => {
			render(<RouterProvider router={router} />);
		});

		const heroButton = screen.getByText("Play");
		act(() => {
			heroButton.focus();
			heroButton.click();
		});

		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/player-selection");
		});
	});
});
