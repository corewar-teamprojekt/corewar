import {
	useDispatchUser,
	useUser,
} from "@/services/userContext/UserContextHelpers.ts";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, Mock, vi } from "vitest";
import PlayerSelection from "./PlayerSelection";

import "@testing-library/jest-dom";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";
import PlayerCodingPage from "../playerCodeInput/PlayerCodingPage";
import { User } from "@/domain/User.ts";

vi.mock("@/services/userContext/UserContextHelpers.ts", () => ({
	useDispatchUser: vi.fn(),
}));

const testRouterConfig = [
	{
		path: "/",
		element: <Navigate to="/player-selection" replace={true} />,
	},
	{
		path: "/player-coding",
		element: <PlayerCodingPage />,
	},
	{
		path: "/player-selection",
		element: <PlayerSelection />,
	},
];

vi.mock("@/services/userContext/UserContextHelpers");

describe("PlayerSelection", () => {
	beforeEach(() =>
		(useUser as Mock).mockReturnValue(new User("playerA", "#ffeefff")),
	);

	it("renders Player A and Player B buttons", () => {
		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);

		expect(screen.getByText("PLAYER A")).toBeInTheDocument();
		expect(screen.getByText("PLAYER B")).toBeInTheDocument();
		expect(screen.getAllByText("PLAY")).toHaveLength(2);
	});

	it("dispatches setPlayerA action and navigates to /player-coding when Player A button is clicked", async () => {
		const mockDispatcher = vi.fn();
		(useDispatchUser as Mock).mockReturnValue(mockDispatcher);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);

		fireEvent.click(screen.getAllByText("PLAY")[0]);

		expect(mockDispatcher).toHaveBeenCalledWith({
			type: "setPlayerA",
			user: null,
		});
		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/player-coding");
		});
	});

	it("dispatches setPlayerB action and navigates to /player-coding when Player B button is clicked", async () => {
		const mockDispatcher = vi.fn();
		(useDispatchUser as Mock).mockReturnValue(mockDispatcher);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);

		fireEvent.click(screen.getAllByText("PLAY")[1]);

		expect(mockDispatcher).toHaveBeenCalledWith({
			type: "setPlayerB",
			user: null,
		});
		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/player-coding");
		});
	});
});
