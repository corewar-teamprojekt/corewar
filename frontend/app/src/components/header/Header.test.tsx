import { afterEach, beforeEach, describe, expect, it, Mock, vi } from "vitest";
import { act, useEffect } from "react";
import { UserProvider } from "@/services/userContext/UserContext.tsx";
import Header from "@/components/header/Header.tsx";
import { cleanup, render, screen } from "@testing-library/react";
import { useDispatchUser } from "@/services/userContext/UserContextHelpers.ts";
import { useLocation } from "react-router-dom";
import { LobbyProvider } from "@/services/lobbyContext/LobbyContext.tsx";
import { useDispatchLobby } from "@/services/lobbyContext/LobbyContextHelpers.ts";
import { Lobby } from "@/domain/Lobby.ts";
import { aLobby } from "@/TestFactories.ts";

vi.mock("react-router-dom");

beforeEach(() => {
	(useLocation as Mock).mockReturnValue({ pathname: "/" });
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
				"#FF0000",
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
				"#0000FF",
			);
		});
	});
});

describe("lobby info", () => {
	it("display current lobby id", () => {
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
		const lobbyInfoButton = screen.getByRole("button");
		expect(lobbyInfoButton).toBeTruthy();
		expect(lobbyInfoButton.textContent).toContain(`Lobby ID: ${lobbyId}`);
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
}: {
	dispatcherCommand: string | null;
	lobby: Lobby | null;
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

	return <Header />;
};
