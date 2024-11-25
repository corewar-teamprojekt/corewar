import { GameState } from "./GameState";

export interface LobbyStatus {
	playerASubmitted: boolean;
	playerBSubmitted: boolean;
	gameState: GameState;
	result: {
		winner: "A" | "B" | "DRAW";
	};
}
