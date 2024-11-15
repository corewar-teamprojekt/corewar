import { MAX_PLAYERS_PER_LOBBY } from "@/consts";
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
		this.isLobbyFull = this.isLobbyFull.bind(this);
		this.equals = this.equals.bind(this);
	}

	isLobbyFull() {
		return this.playersJoined.length >= MAX_PLAYERS_PER_LOBBY;
	}

	equals(lobby: Lobby) {
		let playersJoinedAreEqual = true;
		this.playersJoined.forEach((player, index) => {
			if (player !== lobby.playersJoined[index]) {
				playersJoinedAreEqual = false;
			}
		});
		return (
			this.id === lobby.id &&
			this.gameState === lobby.gameState &&
			playersJoinedAreEqual
		);
	}
}
