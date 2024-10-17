import { GameState } from "@/domain/GameState.ts";
import { GameWinner } from "@/domain/GameWinner.ts";

export interface StatusResponse {
	playerASubmitted: boolean;
	playerBSubmitted: boolean;
	gameState: GameState;
	result: {
		winner: GameWinner;
	};
}
