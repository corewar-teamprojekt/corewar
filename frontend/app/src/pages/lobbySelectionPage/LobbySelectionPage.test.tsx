import { Button } from "@/components/ui/button";
import { BASE_POLLING_INTERVAL_MS } from "@/consts";
import { Lobby } from "@/domain/Lobby";
import { getLobbiesV1 } from "@/services/rest/LobbyRest";
import { aLobby, mockLobbies } from "@/TestFactories";
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
import { useDispatchLobby } from "@/services/lobbyContext/LobbyContextHelpers";

vi.mock("@/services/rest/LobbyRest", () => ({
	getLobbiesV1: vi.fn(),
}));

vi.mock("@/services/lobbyContext/LobbyContextHelpers", () => ({
	useDispatchLobby: vi.fn(),
}));

//mock the LobbySelection component to better see what lobbies are passed down to the LobbySelection component
vi.mock("../../components/LobbySelection/LobbySelection", () => ({
	default: vi.fn(({ lobbies, joinLobby }) => {
		return (
			<div>
				{lobbies.map((lobby: Lobby) => (
					<Button
						key={"test-button-" + lobby.id}
						onClick={() => joinLobby(lobby)}
					>
						{lobby.id}:
						{lobby.isLobbyFull() || lobby.isDisabled ? "disabled" : "enabled"}
					</Button>
				))}
			</div>
		);
	}),
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

	it("joins correct lobby and navigates to player selection on join lobby button click", async () => {
		(getLobbiesV1 as Mock).mockResolvedValue([aLobby()]);
		const mockDispatcher = vi.fn();
		(useDispatchLobby as Mock).mockReturnValue(mockDispatcher);
		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);

		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(1);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		act(() => {
			fireEvent.click(screen.getByText(aLobby().id + ":enabled"));
		});

		await waitFor(() => {
			expect(mockDispatcher.mock.calls[0][0].type).toBe("join");
			expect(
				aLobby().equals(mockDispatcher.mock.calls[0][0].lobby),
			).toBeTruthy();
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

	it("expect that only free lobbies are set after first call", async () => {
		const allPossibleLobbies = mockLobbies();
		(getLobbiesV1 as Mock).mockResolvedValue(allPossibleLobbies);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(1);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		const lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		for (const lobby of allPossibleLobbies) {
			//if the lobby is not full, it should be on screen
			if (lobby.isLobbyFull() === false) {
				expect(
					allDisplayedLobbies.find((ld) => ld.id === lobby.id),
				).toBeTruthy();
			}
		}
	});

	it("expect that a new free lobby is added at the end", async () => {
		const allPossibleLobbies = mockLobbies();
		(getLobbiesV1 as Mock).mockResolvedValue(allPossibleLobbies);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(1);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		let lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allOriginallyDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		for (const lobby of allPossibleLobbies) {
			//if the lobby is not full, it should be on screen
			if (lobby.isLobbyFull() === false) {
				expect(
					allOriginallyDisplayedLobbies.find((ld) => ld.id === lobby.id),
				).toBeTruthy();
			}
		}
		allPossibleLobbies.push(aLobby());
		(getLobbiesV1 as Mock).mockResolvedValue(allPossibleLobbies);
		//wait for next poll
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(2);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allCurrentlyDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		//the length should increase by one
		expect(allCurrentlyDisplayedLobbies.length).toBe(
			allOriginallyDisplayedLobbies.length + 1,
		);

		expect(
			allCurrentlyDisplayedLobbies[allCurrentlyDisplayedLobbies.length - 1].id,
		).toBe(aLobby().id);
	});

	it("expect that already diplayed lobbies do not vanish once they are full", async () => {
		const allPossibleLobbies = mockLobbies();
		(getLobbiesV1 as Mock).mockResolvedValue(allPossibleLobbies);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(1);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		let lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allInitialDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		for (const lobby of allPossibleLobbies) {
			//if the lobby is not full, it should be on screen
			if (lobby.isLobbyFull() === false) {
				expect(
					allInitialDisplayedLobbies.find((ld) => ld.id === lobby.id),
				).toBeTruthy();
			}
		}
		const fullLobbies = fillUpLobbies(allPossibleLobbies);
		(getLobbiesV1 as Mock).mockResolvedValue(fullLobbies);
		//wait for next poll
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(2);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();

		const allCurrentlyDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);

		//since every lobby is full, they should all be disabled
		expect(
			lobbyIdsWithStatus.every((is) => is.status === "disabled"),
		).toBeTruthy();
		//all originally displayed lobbies that are now full should still be displayed
		for (const lobby of allInitialDisplayedLobbies) {
			expect(
				allCurrentlyDisplayedLobbies.find(
					(currentLobby) => currentLobby.id === lobby.id,
				),
			).toBeTruthy();
		}
	});

	it("expect that already diplayed do not vanish once they are deleted", async () => {
		const allPossibleLobbies = mockLobbies();
		(getLobbiesV1 as Mock).mockResolvedValue(allPossibleLobbies);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(1);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);

		let lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allInitialDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		for (const lobby of allPossibleLobbies) {
			//if the lobby is not full, it should be on screen
			if (lobby.isLobbyFull() === false) {
				expect(
					allInitialDisplayedLobbies.find((ld) => ld.id === lobby.id),
				).toBeTruthy();
			}
		}
		(getLobbiesV1 as Mock).mockResolvedValue([]);
		//wait for next poll
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(2);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allCurrentlyDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		//since every lobby is full, they should all be disabled
		expect(
			lobbyIdsWithStatus.every((is) => is.status === "disabled"),
		).toBeTruthy();

		//all originally displayed lobbies that are deleted should still be displayed
		for (const lobby of allInitialDisplayedLobbies) {
			expect(
				allCurrentlyDisplayedLobbies.find(
					(currentLobby) => currentLobby.id === lobby.id,
				),
			).toBeTruthy();
		}
	});

	it("expect that deleted lobbies will be replaced with new ones", async () => {
		const allPossibleLobbies = mockLobbies();
		(getLobbiesV1 as Mock).mockResolvedValue(allPossibleLobbies);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(1);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);

		let lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allInitialDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		for (const lobby of allPossibleLobbies) {
			//if the lobby is not full, it should be on screen
			if (lobby.isLobbyFull() === false) {
				expect(
					allInitialDisplayedLobbies.find((ld) => ld.id === lobby.id),
				).toBeTruthy();
			}
		}
		(getLobbiesV1 as Mock).mockResolvedValue([]);
		//wait for next poll
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(2);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allDeletedDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		//since every lobby is full, they should all be disabled
		expect(
			lobbyIdsWithStatus.every((is) => is.status === "disabled"),
		).toBeTruthy();

		//all originally displayed lobbies that are deleted should still be displayed
		for (const lobby of allInitialDisplayedLobbies) {
			expect(
				allDeletedDisplayedLobbies.find(
					(currentLobby) => currentLobby.id === lobby.id,
				),
			).toBeTruthy();
		}

		allPossibleLobbies.push(aLobby());
		(getLobbiesV1 as Mock).mockResolvedValue([aLobby()]);
		//wait for next poll
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(3);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allCurrentlyDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		//the length should not change
		expect(allCurrentlyDisplayedLobbies.length).toBe(
			allDeletedDisplayedLobbies.length,
		);

		expect(
			allCurrentlyDisplayedLobbies.find(
				(currentLobby) => currentLobby.id === aLobby().id,
			),
		).toBeTruthy();
	});

	it("expect that full lobbies will be replaced with new ones", async () => {
		const allPossibleLobbies = mockLobbies();
		(getLobbiesV1 as Mock).mockResolvedValue(allPossibleLobbies);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(1);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);

		let lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allInitialDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		for (const lobby of allPossibleLobbies) {
			//if the lobby is not full, it should be on screen
			if (lobby.isLobbyFull() === false) {
				expect(
					allInitialDisplayedLobbies.find((ld) => ld.id === lobby.id),
				).toBeTruthy();
			}
		}
		const fullLobbies = fillUpLobbies(allPossibleLobbies);
		(getLobbiesV1 as Mock).mockResolvedValue(fullLobbies);
		//wait for next poll
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(2);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allDeletedDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		//since every lobby is full, they should all be disabled
		expect(
			lobbyIdsWithStatus.every((is) => is.status === "disabled"),
		).toBeTruthy();

		//all originally displayed lobbies that are deleted should still be displayed
		for (const lobby of allInitialDisplayedLobbies) {
			expect(
				allDeletedDisplayedLobbies.find(
					(currentLobby) => currentLobby.id === lobby.id,
				),
			).toBeTruthy();
		}

		allPossibleLobbies.push(aLobby());
		(getLobbiesV1 as Mock).mockResolvedValue([aLobby()]);
		//wait for next poll
		await waitFor(
			() => {
				expect(getLobbiesV1).toHaveBeenCalledTimes(3);
			},
			{ timeout: BASE_POLLING_INTERVAL_MS + 100 },
		);
		lobbyIdsWithStatus = getAllLobbyIdWithStatusFromScreen();
		const allCurrentlyDisplayedLobbies = allPossibleLobbies.filter((lobby) =>
			lobbyIdsWithStatus.find((is) => is.id === lobby.id),
		);
		//the length should not change
		expect(allCurrentlyDisplayedLobbies.length).toBe(
			allDeletedDisplayedLobbies.length,
		);

		expect(
			allCurrentlyDisplayedLobbies.find(
				(currentLobby) => currentLobby.id === aLobby().id,
			),
		).toBeTruthy();
	});
	//Helper functions:
	interface IdWithStatus {
		id: number;
		status: string;
	}

	function getAllLobbyIdWithStatusFromScreen(): IdWithStatus[] {
		const result: IdWithStatus[] = [];
		screen.getAllByRole("button").forEach((button) => {
			if (button.textContent === "CREATE LOBBY") {
				return;
			}
			const splittedText = button.textContent?.split(":");
			result.push({
				id: parseInt(splittedText![0]),
				status: splittedText![1],
			});
		});
		return result;
	}

	function fillUpLobbies(lobbies: Lobby[]): Lobby[] {
		return lobbies.map((lobby) => {
			if (lobby.playersJoined.length === 1) {
				lobby.playersJoined.push("Rainer Winkler");
			}
			return lobby;
		});
	}
});
