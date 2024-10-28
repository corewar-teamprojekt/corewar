import { getStatusV0 } from "@/services/rest/RestService";
import { useUser } from "@/services/userContext/UserContextHelpers";
import "@testing-library/jest-dom";
import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
} from "@testing-library/react";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, Mock, vi } from "vitest";
import ResultDisplayPage from "./ResultDisplayPage";

// Mock the getStatusV0 function
vi.mock("@/services/rest/RestService", () => ({
	getStatusV0: vi.fn(),
}));
vi.mock("@/services/userContext/UserContextHelpers");

const testRouterConfig = [
	{
		path: "/",
		element: <Navigate to="/result-display" replace={true} />,
	},
	{
		path: "/result-display",
		element: <ResultDisplayPage />,
	},
	{
		path: "/player-selection",
		element: <div>hehe :3</div>,
	},
];

const mockResult = {
	playerASubmitted: true,
	playerBSubmitted: true,
	gameState: "FINISHED",
	result: {
		winner: "A",
	},
};

const mockUser = { name: "testUser" };

describe("ResultDisplayPage", () => {
	beforeEach(() => {
		(useUser as Mock).mockReturnValue(mockUser);
	});

	afterEach(() => {
		vi.clearAllMocks();
	});

	it("should make an API request and display the result", async () => {
		const router = createMemoryRouter(testRouterConfig);

		(getStatusV0 as Mock).mockResolvedValue({
			ok: true,
			json: async () => mockResult,
		});

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(() => {
			expect(getStatusV0).toHaveBeenCalled();
		});

		//just test if the fields from the MockResponse are in the document, if they are displayed correctly should be tested in the JsonDisplay.test.tsx
		expect(await screen.findByText(/playerASubmitted/)).toBeInTheDocument();
		expect(await screen.findByText(/playerBSubmitted/)).toBeInTheDocument();
		expect(await screen.findByText(/gameState/)).toBeInTheDocument();
		expect(await screen.findByText(/result/)).toBeInTheDocument();
		expect(await screen.findByText(/winner/)).toBeInTheDocument();
	});

	it("should navigate to player selection page on button click", async () => {
		const router = createMemoryRouter(testRouterConfig);

		(getStatusV0 as Mock).mockResolvedValue({
			ok: true,
			json: async () => mockResult,
		});

		act(() => {
			render(<RouterProvider router={router} />);
		});
		act(() => {
			const button = screen.getByRole("button", { name: /Play again/i });
			fireEvent.click(button);
		});
		expect(router.state.location.pathname).toEqual("/player-selection");
	});
});
