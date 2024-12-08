import { GameState } from "./GameState";

export interface LobbyStatus {
	playerASubmitted: boolean;
	playerBSubmitted: boolean;
	gameState: GameState;
	result: {
		winner: "A" | "B" | "DRAW";
	};
	visualizationData: VisuStep[];
}

interface VisuStep {
	playerId: "playerA" | "playerB";
	programCounterBefore: number;
	programCounterAfter: number;
	programCountersOfOtherProcesses: number[];
	memoryReads: number[];
	memoryWrites: number[];
	processDied: boolean;
}
