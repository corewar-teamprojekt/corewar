import { beforeEach, describe, it, vi, expect, Mock } from "vitest";
import { cleanup, render, waitFor } from "@testing-library/react";
import { act } from "react";
import WaitingForResultPage from "@/pages/waitingForResult/WaitingForResultPage.tsx";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";
import { POLLING_INTERVAL_MS } from "@/pages/waitingForResult/consts.ts";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { User } from "@/domain/user";

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

// Might theoretically become flaky, since its working with actual timers
describe("backend polling", () => {
	beforeEach(() =>
		(useUser as Mock).mockReturnValue(new User("PlayerA", "#ffeeff")),
	);

	it("starts polling correct endpoint once component gets rendered", async () => {
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
				expect(mockFetch).toHaveBeenCalledWith("https://backend/api/status");
			},
			{
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
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
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(2);
			},
			{
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(3);
			},
			{
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
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
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(2);
			},
			{
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(3);
			},
			{
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
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
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		expect(router.state.location.pathname).toEqual("/result-display");
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
				timeout: POLLING_INTERVAL_MS + POLLING_BUFFER,
			},
		);

		await new Promise((r) =>
			setTimeout(r, POLLING_INTERVAL_MS + POLLING_BUFFER),
		);

		expect(mockFetch).toHaveBeenCalledTimes(1);

		await new Promise((r) =>
			setTimeout(r, POLLING_INTERVAL_MS + POLLING_BUFFER),
		);

		expect(mockFetch).toHaveBeenCalledTimes(1);
	});
});
