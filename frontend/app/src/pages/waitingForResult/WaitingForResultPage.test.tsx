import { beforeEach, describe, it, vi, expect, Mock } from "vitest";
import { cleanup, render, waitFor } from "@testing-library/react";
import { act } from "react";
import WaitingForResultPage from "@/pages/waitingForResult/WaitingForResultPage.tsx";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { User } from "@/domain/User.ts";
import { BASE_POLLING_INTERVAL_MS } from "@/consts.ts";
import { aLobby } from "@/TestFactories.ts";

const POLLING_BUFFER = 500;

const testRouterConfig = [
	{
		path: "/",
		element: <Navigate to="/waiting-for-result" replace={true} />,
	},
	{
		path: "/waiting-for-result",
		element: <WaitingForResultPage />,
	},
	{
		path: "/result-display",
		element: <div>Result display</div>,
	},
];

beforeEach(() => {
	cleanup();
	vi.restoreAllMocks();
});

vi.mock("@/services/userContext/UserContextHelpers");

// Might theoretically become flaky, since it's working with actual timers
describe("backend polling", () => {
	beforeEach(() =>
		(useUser as Mock).mockReturnValue(new User("playerA", "#ffeeff")),
	);

	it("starts polling correct endpoint once component gets rendered", async () => {
		const LOBBY_ID = 0;
		vi.mock("@/services/lobbyContext/LobbyContextHelpers", () => ({
			useDispatchLobby: vi.fn(),
			useLobby: () => aLobby({ lobbyId: 0 }), // Lobby id from the top
		}));

		// Mock the fetch function
		const mockFetch = vi.fn(() =>
			Promise.resolve({
				json: () =>
					Promise.resolve({
						gameState: "RUNNING",
					}),
			}),
		);

		// This is just mocking for testing, so don't care what my linter says :3
		// eslint-disable-next-line
		// @ts-ignore
		global.fetch = mockFetch;

		const router = createMemoryRouter(testRouterConfig);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledWith(
					`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v1/lobby/${LOBBY_ID}/status`,
				);
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);
	});

	it("keeps polling when gameState is not FINISHED", async () => {
		// Mock the fetch function
		const mockFetch = vi.fn(() =>
			Promise.resolve({
				json: () =>
					Promise.resolve({
						gameState: "RUNNING",
					}),
			}),
		);

		// This is just mocking for testing, so don't care what my linter says :3
		// eslint-disable-next-line
		// @ts-ignore
		global.fetch = mockFetch;

		const router = createMemoryRouter(testRouterConfig);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(1);
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(2);
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(3);
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);
	});

	it("keeps polling on server error", async () => {
		// Mock the fetch function
		const mockFetch = vi.fn(() =>
			Promise.resolve({
				ok: false,
				status: 500,
				json: () => Promise.resolve({ message: "Internal Server Error" }),
			}),
		);

		// This is just mocking for testing, so don't care what my linter says :3
		// eslint-disable-next-line
		// @ts-ignore
		global.fetch = mockFetch;

		const router = createMemoryRouter(testRouterConfig);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(1);
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(2);
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(3);
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);
	});

	it("routes to correct page once game is finished", async () => {
		// Mock the fetch function
		const mockFetch = vi.fn(() =>
			Promise.resolve({
				json: () =>
					Promise.resolve({
						gameState: "FINISHED",
					}),
			}),
		);

		// This is just mocking for testing, so don't care what my linter says :3
		// eslint-disable-next-line
		// @ts-ignore
		global.fetch = mockFetch;

		const router = createMemoryRouter(testRouterConfig);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledOnce();
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		expect(router.state.location.pathname).toEqual("/visu");
	});

	it("stops polling once game is finished", async () => {
		// Mock the fetch function
		const mockFetch = vi.fn(() =>
			Promise.resolve({
				json: () =>
					Promise.resolve({
						gameState: "FINISHED",
					}),
			}),
		);

		// This is just mocking for testing, so don't care what my linter says :3
		// eslint-disable-next-line
		// @ts-ignore
		global.fetch = mockFetch;

		const router = createMemoryRouter(testRouterConfig);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		console.log(router.state.location.pathname);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledOnce();
			},
			{
				timeout: BASE_POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await new Promise((r) =>
			setTimeout(r, BASE_POLLING_INTERVAL_MS + POLLING_BUFFER),
		);

		expect(mockFetch).toHaveBeenCalledTimes(1);

		await new Promise((r) =>
			setTimeout(r, BASE_POLLING_INTERVAL_MS + POLLING_BUFFER),
		);

		expect(mockFetch).toHaveBeenCalledTimes(1);
	});
});
