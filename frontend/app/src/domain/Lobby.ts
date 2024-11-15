import { GameState } from "@/domain/GameState.ts";

export class Lobby {
	public id: number;
	public playersJoined: string[];
	public gameState: GameState;
	public isDisabled: boolean | undefined;

	constructor(id: number, playersJoined: string[], gameState: GameState) {
		this.id = id;
		this.playersJoined = playersJoined;
		this.gameState = gameState;
	}
}
