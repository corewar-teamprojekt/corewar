import { describe, expect, it } from "vitest";
import { setupServer } from "msw/node";
import { http, HttpResponse } from "msw";
import { GameState } from "@/domain/GameState.ts";
import { createLobbyV1, getLobbiesV1 } from "@/services/rest/LobbyRest.ts";
import { Lobby } from "@/domain/Lobby.ts";

describe("getLobbiesV1", () => {
	it("correctly parses json into lobbies", async () => {
		const lobbyId: number = 1;
		const playersJoined: string[] = ["playerA"];
		const gameState: GameState = GameState.NOT_STARTED;

		const restHandlers = [
			http.get("http://localhost:8080/api/v1/lobby", () => {
				return HttpResponse.json({
					lobbies: [
						{
							id: lobbyId,
							playersJoined: playersJoined,
							gameState: gameState,
						},
					],
				});
			}),
		];

		const server = setupServer(...restHandlers);
		server.listen({ onUnhandledRequest: "error" });

		const parsedLobbies = await getLobbiesV1();
		const expectedLobbies = [new Lobby(lobbyId, playersJoined, gameState)];

		expect(expectedLobbies.length).toEqual(parsedLobbies.length);
		expect(expectedLobbies[0].equals(parsedLobbies[0])).toBeTruthy();

		server.close();
	});
});

describe("createLobbyV1", () => {
	it("returns lobby id from http request", async () => {
		const lobbyId: string = "1";

		const restHandlers = [
			http.post("http://localhost:8080/api/v1/lobby", () => {
				return HttpResponse.json({
					lobbyId: lobbyId,
				});
			}),
		];

		const server = setupServer(...restHandlers);
		server.listen({ onUnhandledRequest: "error" });

		expect(await createLobbyV1("playerA")).toEqual(lobbyId);

		server.close();
	});
});
