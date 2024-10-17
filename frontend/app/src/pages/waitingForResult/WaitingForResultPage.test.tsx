import { beforeEach, describe, it, vi, expect } from "vitest";
import { cleanup, render, waitFor } from "@testing-library/react";
import { act } from "react";
import WaitingForResultPage from "@/pages/waitingForResult/WaitingForResultPage.tsx";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";

beforeEach(() => {
	cleanup();
	vi.restoreAllMocks();
});

describe("backend polling", () => {
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

		// TODO: move router config to single source of truth
		const router = createMemoryRouter([
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
		]);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledWith("https://backend/api/status");
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
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

		const router = createMemoryRouter([
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
		]);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(1);
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(2);
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(3);
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
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

		const router = createMemoryRouter([
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
		]);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(1);
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(2);
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
			},
		);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledTimes(3);
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
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

		const router = createMemoryRouter([
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
		]);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledOnce();
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
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

		const router = createMemoryRouter([
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
		]);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		console.log(router.state.location.pathname);

		await waitFor(
			() => {
				expect(mockFetch).toHaveBeenCalledOnce();
			},
			{
				// 1 * Polling Interval + Polling Interval
				// TODO: Extract Polling interval to some kind of consts file
				timeout: 2000,
			},
		);

		await new Promise((r) => setTimeout(r, 1100));

		expect(mockFetch).toHaveBeenCalledTimes(1);

		await new Promise((r) => setTimeout(r, 1100));

		expect(mockFetch).toHaveBeenCalledTimes(1);
	});
});
