import { afterEach, beforeEach, describe, expect, it, Mock, vi } from "vitest";
import { act, useEffect } from "react";
import { UserProvider } from "@/services/userContext/UserContext.tsx";
import Header from "@/components/header/Header.tsx";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import { useDispatchUser } from "@/services/userContext/UserContextHelpers.ts";
import { useLocation } from "react-router-dom";
import { LobbyProvider } from "@/services/lobbyContext/LobbyContext.tsx";
import { useDispatchLobby } from "@/services/lobbyContext/LobbyContextHelpers.ts";
import { Lobby } from "@/domain/Lobby.ts";
import { aLobby } from "@/TestFactories.ts";
import { useToast } from "@/hooks/use-toast.ts";

vi.mock("react-router-dom");

vi.mock("@/hooks/use-toast", () => ({
	useToast: vi.fn(),
}));

Object.assign(navigator, {
	clipboard: {
		writeText: () => {},
	},
});

describe("test header", () => {
	const mockToast = vi.fn();

	beforeEach(() => {
		(useLocation as Mock).mockReturnValue({ pathname: "/" });
		(useToast as Mock).mockReturnValue({ toast: mockToast });
		cleanup();
	});

	afterEach(() => {
		vi.clearAllMocks();
	});

	describe("playerIndicator", () => {
		describe("doesn't display anything when user is null", () => {
			it("playerName", () => {
				act(() => {
					render(
						<UserProvider>
							<UserDispatcherInteractor dispatcherCommand={null} />
						</UserProvider>,
					);
				});

				expect(screen.queryByText("Player")).toBeFalsy();
			});

			it("playerColor", () => {
				act(() => {
					render(
						<UserProvider>
							<UserDispatcherInteractor dispatcherCommand={null} />
						</UserProvider>,
					);
				});

				const circle = screen.queryByRole("img");

				expect(circle).toBeFalsy();
			});
		});

		describe("displays userName sourced from userContext", () => {
			it("playerA", () => {
				act(() => {
					render(
						<UserProvider>
							<UserDispatcherInteractor dispatcherCommand={"setPlayerA"} />
						</UserProvider>,
					);
				});

				expect(screen.getByText("playerA")).toBeTruthy();
			});

			it("playerB", () => {
				act(() => {
					render(
						<UserProvider>
							<UserDispatcherInteractor dispatcherCommand={"setPlayerB"} />
						</UserProvider>,
					);
				});

				expect(screen.getByText("playerB")).toBeTruthy();
			});
		});

		describe("displays correct playerColor", () => {
			it("render correct color for playerA", () => {
				act(() => {
					render(
						<UserProvider>
							<UserDispatcherInteractor dispatcherCommand={"setPlayerA"} />
						</UserProvider>,
					);
				});
				const circle = screen.getByRole("img");

				expect(circle).toBeTruthy();
				expect(circle.querySelector("circle")?.getAttribute("fill")).toEqual(
					"#FF006E",
				);
			});

			it("render correct color for playerA", () => {
				act(() => {
					render(
						<UserProvider>
							<UserDispatcherInteractor dispatcherCommand={"setPlayerB"} />
						</UserProvider>,
					);
				});
				const circle = screen.getByRole("img");

				expect(circle).toBeTruthy();
				expect(circle.querySelector("circle")?.getAttribute("fill")).toEqual(
					"#00FFFF",
				);
			});
		});
	});

	describe("lobby info", () => {
		it("display current lobby id", async () => {
			const lobbyId: number = 0;
			const anotherLobbyId: number = 1;

			act(() => {
				render(
					<LobbyProvider>
						<LobbyDispatcherInteractor
							dispatcherCommand={"join"}
							lobby={aLobby({ lobbyId: lobbyId })}
							secondaryLobbyId={anotherLobbyId}
						/>
					</LobbyProvider>,
				);
			});
			const lobbyInfoButton = screen.getByRole("button", { name: /Lobby ID:/ });
			expect(lobbyInfoButton).toBeTruthy();
			expect(lobbyInfoButton.textContent).toEqual(`Lobby ID: ${lobbyId}`);

			act(() => {
				screen.getByTestId("joinSecondary").click();
			});
			await waitFor(
				() => {
					expect(lobbyInfoButton.textContent).toEqual(
						`Lobby ID: ${anotherLobbyId}`,
					);
				},
				{ timeout: 1000 },
			);
		});

		it("hides if lobby is null", () => {
			act(() => {
				render(
					<LobbyProvider>
						<LobbyDispatcherInteractor
							dispatcherCommand={"leave"}
							lobby={null}
						/>
					</LobbyProvider>,
				);
			});

			const lobbyInfoButton = screen.queryAllByRole("button", {
				name: /Lobby ID:/,
			});
			expect(lobbyInfoButton).toHaveLength(0);
		});

		it("copies lobby id to clipboard on click", async () => {
			const spy = vi.spyOn(navigator.clipboard, "writeText");

			const lobbyId: number = 0;
			const anotherLobbyId: number = 1;

			act(() => {
				render(
					<LobbyProvider>
						<LobbyDispatcherInteractor
							dispatcherCommand={"join"}
							lobby={aLobby({ lobbyId: lobbyId })}
							secondaryLobbyId={anotherLobbyId}
						/>
					</LobbyProvider>,
				);
			});

			const lobbyInfoButton = screen.getByRole("button", { name: /Lobby ID:/ });

			lobbyInfoButton.click();

			expect(spy).toHaveBeenCalledTimes(1);
			expect(spy).toHaveBeenLastCalledWith(`${lobbyId}`);

			act(() => {
				screen.getByTestId("joinSecondary").click();
			});
			await waitFor(
				() => {
					expect(lobbyInfoButton.textContent).toEqual(
						`Lobby ID: ${anotherLobbyId}`,
					);
				},
				{ timeout: 1000 },
			);

			lobbyInfoButton.click();

			expect(spy).toHaveBeenCalledTimes(2);
			expect(spy).toHaveBeenLastCalledWith(`${anotherLobbyId}`);
		});

		it("displays toast on click", async () => {
			const lobbyId: number = 0;

			act(() => {
				render(
					<LobbyProvider>
						<LobbyDispatcherInteractor
							dispatcherCommand={"join"}
							lobby={aLobby({ lobbyId: lobbyId })}
						/>
					</LobbyProvider>,
				);
			});

			const lobbyInfoButton = screen.getByRole("button", { name: /Lobby ID:/ });

			expect(mockToast).toHaveBeenCalledTimes(0);
			lobbyInfoButton.click();
			await waitFor(
				() => {
					expect(mockToast).toHaveBeenCalledTimes(1);
				},
				{ timeout: 100 },
			);
			expect(mockToast).toHaveBeenCalledWith({
				title: "Copied lobbyID to clipboard",
				description: "",
				variant: "default",
			});
		});
	});
});

// Used to pass commands to the UserContext for testing
const UserDispatcherInteractor = ({
	dispatcherCommand,
}: {
	dispatcherCommand: string | null;
}) => {
	const dispatch = useDispatchUser();

	useEffect(() => {
		act(() => {
			if (dispatch && dispatcherCommand) {
				dispatch({
					type: dispatcherCommand,
					user: null,
				});
			}
		});
	}, [dispatch, dispatcherCommand]);

	return <Header />;
};

// Used to pass commands to the LobbyContext for testing
const LobbyDispatcherInteractor = ({
	dispatcherCommand,
	lobby,
	secondaryLobbyId = 1,
}: {
	dispatcherCommand: string | null;
	lobby: Lobby | null;
	secondaryLobbyId?: number;
}) => {
	const dispatch = useDispatchLobby();

	useEffect(() => {
		act(() => {
			if (dispatch && dispatcherCommand) {
				dispatch({
					type: dispatcherCommand,
					lobby: lobby,
				});
			}
		});
	}, [dispatch, dispatcherCommand, lobby]);

	const leave = () => {
		if (dispatch) {
			dispatch({
				type: "leave",
				lobby: null,
			});
		}
	};

	const joinSecondaryLobby = () => {
		if (dispatch) {
			dispatch({
				type: "join",
				lobby: aLobby({ lobbyId: secondaryLobbyId }),
			});
		}
	};

	return (
		<>
			<Header />
			<button onClick={leave}>leave</button>
			<button onClick={joinSecondaryLobby} data-testid="joinSecondary">
				join secondary
			</button>
		</>
	);
};
