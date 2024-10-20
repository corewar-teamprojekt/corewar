import { it, describe, beforeEach, expect, vi, Mock } from "vitest";
import { cleanup, render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import LandingPage from "@/pages/landingPage/LandingPage.tsx";
import { act } from "react";
import { useUser } from "@/services/userContext/UserContextHelpers.ts";

vi.mock("@/services/userContext/UserContextHelpers");

const testRouterConfig = [
	{
		path: "/",
		element: <LandingPage />,
	},
	{
		path: "/player-selection",
		element: <div>Player Selection</div>,
	},
];

describe("hero button", () => {
	const mockUser = { name: "testUser" };

	beforeEach(() => {
		cleanup();
		vi.restoreAllMocks();
		(useUser as Mock).mockReturnValue(mockUser);
	});

	it("routes to player-selection", () => {
		const router = createMemoryRouter(testRouterConfig);
		act(() => {
			render(<RouterProvider router={router} />);
		});

		const heroButton = screen.getByRole("button");
		act(() => {
			heroButton.focus();
			heroButton.click();
		});

		expect(router.state.location.pathname).toEqual("/player-selection");
	});
});
