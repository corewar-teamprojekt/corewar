import LandingPage from "@/pages/landingPage/LandingPage.tsx";
import PlayerSelection from "@/pages/playerSelection/PlayerSelection.tsx";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import { act } from "react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { beforeEach, describe, expect, it, Mock, vi } from "vitest";

const testRouterConfig = [
	{
		path: "/",
		element: <LandingPage enableThreeJs={false}></LandingPage>,
	},
	{
		path: "/player-selection",
		element: <PlayerSelection />,
	},
	{
		path: "/lobby-selection",
		element: <p>Lobby selection fake</p>,
	},
];

vi.mock("@/services/userContext/UserContextHelpers");

describe("hero button", () => {
	beforeEach(() => {
		(useUser as Mock).mockReturnValue(null);
		cleanup();
	});

	it("routes to lobby-selection", async () => {
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
			expect(router.state.location.pathname).toEqual("/lobby-selection");
		});
	});
});
