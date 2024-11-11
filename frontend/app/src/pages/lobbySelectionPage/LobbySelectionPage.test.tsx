import { BASE_POLLING_INTERVAL_MS } from "@/consts";
import { getLobbiesV1 } from "@/services/rest/RestService";
import { mockLobbies } from "@/TestFactories";
import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
} from "@testing-library/react";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";
import { beforeEach, describe, expect, it, Mock, vi } from "vitest";
import LobbySelectionPage from "./LobbySelectionPage";

vi.mock("@/services/rest/RestService", () => ({
	getLobbiesV1: vi.fn(),
}));

const testRouterConfig = [
	{
		path: "/",
		element: <Navigate to="/lobby-selection" replace={true} />,
	},
	{
		path: "/lobby-selection",
		element: <LobbySelectionPage />,
	},
	{
		path: "/player-selection",
		element: <div>player-selection</div>,
	},
];

describe("LobbySelectionPage", () => {
	beforeEach(() => {
		vi.clearAllMocks();
	});

	it("navigates to player selection on create lobby button click", async () => {
		(getLobbiesV1 as Mock).mockResolvedValue(mockLobbies());

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);

		act(() => {
			fireEvent.click(screen.getByText("CREATE LOBBY"));
		});
		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/player-selection");
		});
	});

	it("polls for lobbies at the specified interval and stops once page changes", async () => {
		(getLobbiesV1 as Mock).mockResolvedValue(mockLobbies());

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);
		const maxPolls = 3;
		for (let i = 1; i <= maxPolls; i++) {
			await waitFor(
				() => {
					expect(getLobbiesV1).toHaveBeenCalledTimes(i);
				},
				{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
			);
		}

		act(() => {
			fireEvent.click(screen.getByText("CREATE LOBBY"));
		});
		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/player-selection");
		});
		await new Promise((resolve) => setTimeout(resolve, 1000));
		expect(getLobbiesV1).toHaveBeenCalledTimes(maxPolls);
	});
});
