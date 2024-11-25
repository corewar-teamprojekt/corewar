import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
} from "@testing-library/react";
import { beforeEach, describe, expect, it, Mock, vi } from "vitest";
import PlayerSelection from "./PlayerSelection";

import { Toaster } from "@/components/ui/toaster";
import { Lobby } from "@/domain/Lobby";
import { LobbyProvider } from "@/services/lobbyContext/LobbyContext";
import {
	useDispatchLobby,
	useLobby,
} from "@/services/lobbyContext/LobbyContextHelpers";
import { createLobbyV1, joinLobbyV1 } from "@/services/rest/LobbyRest";
import { UserProvider } from "@/services/userContext/UserContext";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { aLobby, anotherLobby } from "@/TestFactories";
import "@testing-library/jest-dom";
import { useEffect } from "react";
import type { RouterProviderProps } from "react-router-dom";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";

const testRouterConfig = [
	{
		path: "/",
		element: <Navigate to="/player-selection" replace={true} />,
	},
	{
		path: "/player-coding",
		element: <TestLobbyAndUserPage />,
	},
	{
		path: "/lobby-selection",
		element: <TestLobbyAndUserPage />,
	},
	{
		path: "/player-selection",
		element: (
			<>
				<PlayerSelection />
				<Toaster />
			</>
		),
	},
];

vi.mock("@/services/rest/LobbyRest", () => ({
	createLobbyV1: vi.fn(),
	joinLobbyV1: vi.fn(),
}));

describe("PlayerSelection", () => {
	beforeEach(() => {
		vi.resetAllMocks();
	});

	it("renders Player A and Player B buttons", () => {
		const router = createMemoryRouter(testRouterConfig);
		render(
			<UserProvider>
				<LobbyProvider>
					<TestRouter router={router} />
					<Toaster />
				</LobbyProvider>
			</UserProvider>,
		);
		const buttonA = screen.getByAltText("Player A Icon");
		const buttonB = screen.getByAltText("Player B Icon");
		expect(buttonA).toBeInTheDocument();
		expect(buttonB).toBeInTheDocument();
	});
	describe("disabled the corresponding button if the player is already in the lobby", () => {
		it("for Button A", async () => {
			const router = createMemoryRouter(testRouterConfig);
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter lobby={aLobby()} router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			const buttonA = screen.getAllByRole("button")[0];
			const buttonB = screen.getAllByRole("button")[1];
			await waitFor(() => {
				expect(buttonA).toBeDisabled();
				expect(buttonB).not.toBeDisabled();
			});
		});
		it("for Button B", async () => {
			const router = createMemoryRouter(testRouterConfig);
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter lobby={anotherLobby()} router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			const buttonA = screen.getAllByRole("button")[0];
			const buttonB = screen.getAllByRole("button")[1];
			await waitFor(() => {
				expect(buttonB).toBeDisabled();
				expect(buttonA).not.toBeDisabled();
			});
		});
	});

	describe("when no lobby is set a new one is created, the player joins it, then redirect occurs", () => {
		it("Player A joins with new lobby", async () => {
			const router = createMemoryRouter(testRouterConfig);
			const expectedLobbyID = 123;
			(createLobbyV1 as Mock).mockResolvedValue(expectedLobbyID);
			(joinLobbyV1 as Mock).mockResolvedValue({});
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			act(() => {
				const buttonA = screen.getAllByRole("button")[0];
				fireEvent.click(buttonA);
			});
			await waitFor(() => {
				expect(createLobbyV1).toHaveBeenCalledWith("playerA");
			});
			await waitFor(() => {
				expect(router.state.location.pathname).toEqual("/player-coding");
			});
			const playerName = screen.getByTestId("playerName").textContent;
			const lobbyId = screen.getByTestId("lobbyId").textContent;
			expect(playerName).toEqual("playerA");
			expect(lobbyId).toEqual(expectedLobbyID.toString());
			expect(joinLobbyV1).not.toHaveBeenCalled();
		});

		it("Player B joins with new lobby", async () => {
			const router = createMemoryRouter(testRouterConfig);
			const expectedLobbyID = 321;
			(createLobbyV1 as Mock).mockResolvedValue(expectedLobbyID);
			(joinLobbyV1 as Mock).mockResolvedValue({});
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			act(() => {
				const buttonB = screen.getAllByRole("button")[1];
				fireEvent.click(buttonB);
			});
			await waitFor(() => {
				expect(createLobbyV1).toHaveBeenCalledWith("playerB");
			});
			await waitFor(() => {
				expect(router.state.location.pathname).toEqual("/player-coding");
			});
			const playerName = screen.getByTestId("playerName").textContent;
			const lobbyId = screen.getByTestId("lobbyId").textContent;
			expect(playerName).toEqual("playerB");
			expect(lobbyId).toEqual(expectedLobbyID.toString());
			expect(joinLobbyV1).not.toHaveBeenCalled();
		});
	});

	describe("when a lobby is set, the player joins it, then redirect occurs", () => {
		it("Player A joins with new lobby", async () => {
			const router = createMemoryRouter(testRouterConfig);
			const expectedLobby = anotherLobby();
			(joinLobbyV1 as Mock).mockResolvedValue({});
			(createLobbyV1 as Mock).mockRejectedValue(
				new Error("Should not be called"),
			);
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter lobby={expectedLobby} router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			act(() => {
				const buttonA = screen.getAllByRole("button")[0];
				fireEvent.click(buttonA);
			});
			await waitFor(() => {
				expect(joinLobbyV1).toHaveBeenCalledWith("playerA", expectedLobby.id);
			});
			await waitFor(() => {
				expect(router.state.location.pathname).toEqual("/player-coding");
			});
			const playerName = screen.getByTestId("playerName").textContent;
			const lobbyId = screen.getByTestId("lobbyId").textContent;
			expect(playerName).toEqual("playerA");
			expect(lobbyId).toEqual(expectedLobby.id.toString());

			expect(createLobbyV1).not.toHaveBeenCalled();
		});
		it("Player B joins with new lobby", async () => {
			const router = createMemoryRouter(testRouterConfig);
			const expectedLobby = aLobby();
			(joinLobbyV1 as Mock).mockResolvedValue({});
			(createLobbyV1 as Mock).mockRejectedValue(
				new Error("Should not be called"),
			);
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter lobby={expectedLobby} router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			act(() => {
				const buttonA = screen.getAllByRole("button")[1];
				fireEvent.click(buttonA);
			});
			await waitFor(() => {
				expect(joinLobbyV1).toHaveBeenCalledWith("playerB", expectedLobby.id);
			});
			await waitFor(() => {
				expect(router.state.location.pathname).toEqual("/player-coding");
			});
			const playerName = screen.getByTestId("playerName").textContent;
			const lobbyId = screen.getByTestId("lobbyId").textContent;
			expect(playerName).toEqual("playerB");
			expect(lobbyId).toEqual(expectedLobby.id.toString());

			expect(createLobbyV1).not.toHaveBeenCalled();
		});
	});

	describe("when joining or creating fails, the user is redirect to lobby-selection", () => {
		it("createlobby Fails", async () => {
			const router = createMemoryRouter(testRouterConfig);
			(joinLobbyV1 as Mock).mockResolvedValue({});
			(createLobbyV1 as Mock).mockRejectedValue(
				new Error("Should not be called"),
			);
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			act(() => {
				const buttonA = screen.getAllByRole("button")[0];
				fireEvent.click(buttonA);
			});
			await waitFor(() => {
				expect(createLobbyV1).toHaveBeenCalled();
			});
			await waitFor(() => {
				expect(router.state.location.pathname).toEqual("/lobby-selection");
			});
		});

		it("joinLobby Fails", async () => {
			const router = createMemoryRouter(testRouterConfig);
			(joinLobbyV1 as Mock).mockRejectedValue(
				new Error("Should not be called"),
			);
			(createLobbyV1 as Mock).mockResolvedValue({});
			act(() => {
				render(
					<UserProvider>
						<LobbyProvider>
							<TestRouter lobby={anotherLobby()} router={router} />
							<Toaster />
						</LobbyProvider>
					</UserProvider>,
				);
			});
			act(() => {
				const buttonA = screen.getAllByRole("button")[0];
				fireEvent.click(buttonA);
			});
			await waitFor(() => {
				expect(joinLobbyV1).toHaveBeenCalled();
			});
			await waitFor(() => {
				expect(router.state.location.pathname).toEqual("/lobby-selection");
			});
		});
	});
});

interface TestRouterProps {
	lobby?: Lobby | null;
	router: RouterProviderProps["router"];
}

function TestRouter({ lobby = null, router }: Readonly<TestRouterProps>) {
	const lobbyDispatch = useDispatchLobby();
	useEffect(() => {
		if (lobby && lobbyDispatch) {
			lobbyDispatch({ type: "join", lobby: lobby });
		}
	}, [lobby, lobbyDispatch]);
	return <RouterProvider router={router} />;
}

function TestLobbyAndUserPage() {
	const lobby = useLobby();
	const user = useUser();
	return (
		<>
			<p data-testid="playerName">{user?.name}</p>
			<p data-testid="lobbyId">{lobby?.id}</p>
		</>
	);
}
