import { LobbyProvider } from "@/services/lobbyContext/LobbyContext.tsx";
import {
	lobbyReducer,
	useDispatchLobby,
	useLobby,
} from "@/services/lobbyContext/LobbyContextHelpers.ts";
import { aLobby, anotherLobby } from "@/TestFactories.ts";
import { cleanup, render, screen } from "@testing-library/react";
import { act } from "react";
import { beforeEach, describe, expect, it } from "vitest";

const NO_LOBBY_TEXT = "No lobby";

describe("lobby state", () => {
	beforeEach(() => {
		cleanup();
	});

	describe("reducer", () => {
		describe("joining", () => {
			it("returns joined lobby when not in lobby", () => {
				expect(
					lobbyReducer(null, { type: "join", lobby: aLobby() })?.equals(
						aLobby(),
					),
				).toBeTruthy();
			});

			it("overwrites active lobby with new lobby", () => {
				expect(
					lobbyReducer(aLobby(), {
						type: "join",
						lobby: anotherLobby(),
					})?.equals(anotherLobby()),
				).toBeTruthy();
			});
		});

		describe("leaving", () => {
			it("stays on null when not in a lobby", () => {
				expect(lobbyReducer(null, { type: "leave", lobby: null })).toEqual(
					null,
				);
			});

			it("returns null when currently in a lobby", () => {
				expect(lobbyReducer(aLobby(), { type: "leave", lobby: null })).toEqual(
					null,
				);
			});
		});

		it("throws error when action type is invalid", () => {
			expect(() =>
				lobbyReducer(aLobby(), { type: "foo", lobby: null }),
			).toThrowError("foo");
		});
	});

	describe("context", () => {
		it("initializes as null", () => {
			act(() => {
				render(
					<LobbyProvider>
						<TestComponent></TestComponent>
					</LobbyProvider>,
				);
			});

			const lobbyId = screen.getByTestId("lobbyId").textContent;
			expect(lobbyId).toEqual(NO_LOBBY_TEXT);
		});

		it("can join lobby", () => {
			act(() => {
				render(
					<LobbyProvider>
						<TestComponent></TestComponent>
					</LobbyProvider>,
				);
			});

			act(() => {
				screen.getByTestId("join").click();
			});

			const lobbyId = screen.getByTestId("lobbyId").textContent;
			expect(lobbyId).toEqual(aLobby().id.toString());
		});

		it("can leave lobby after joining", () => {
			act(() => {
				render(
					<LobbyProvider>
						<TestComponent></TestComponent>
					</LobbyProvider>,
				);
			});

			act(() => {
				screen.getByTestId("join").click();
			});

			act(() => {
				screen.getByTestId("leave").click();
			});

			const lobbyId = screen.getByTestId("lobbyId").textContent;
			expect(lobbyId).toEqual(NO_LOBBY_TEXT);
		});
	});
});

function TestComponent() {
	const lobby = useLobby();
	const lobbyDispatch = useDispatchLobby();

	return (
		<>
			<div data-testid="lobbyId">{lobby ? lobby?.id : NO_LOBBY_TEXT}</div>
			<button
				data-testid="join"
				onClick={() => {
					if (lobbyDispatch) {
						lobbyDispatch({ lobby: aLobby(), type: "join" });
					}
				}}
			>
				Join
			</button>
			<button
				data-testid="leave"
				onClick={() => {
					if (lobbyDispatch) {
						lobbyDispatch({ lobby: null, type: "leave" });
					}
				}}
			>
				Leave
			</button>
		</>
	);
}
