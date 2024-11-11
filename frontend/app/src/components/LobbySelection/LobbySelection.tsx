import { Lobby } from "@/domain/Lobby";
import { Table, TableBody, TableCell, TableRow } from "../ui/table";
import { Button } from "../ui/button";
import { Card } from "../ui/card";
import { Input } from "../ui/input";
import { useState } from "react";

interface LobbySelectionProps {
	lobbies: Lobby[];
	joinLobby: (lobby: Lobby) => void;
}

export default function LobbySelection({
	lobbies,
	joinLobby,
}: Readonly<LobbySelectionProps>) {
	const [searchedLobbyID, setSearchedLobbyID] = useState("");

	//later this will probbalby be unique for every lobby, but for now its always 2
	const MAX_PLAYERS = 2;

	function getStyledButtonForLobby(lobby: Lobby) {
		const isLobbyFull = lobby.playersJoined.length >= MAX_PLAYERS;
		const styling = isLobbyFull
			? "border-2 border-red-900"
			: "border-2 border-lime-700";
		return (
			<Button
				onClick={() => joinLobby(lobby)}
				variant="outline"
				className={styling}
				disabled={isLobbyFull}
			>
				Join
			</Button>
		);
	}

	return (
		<div className="w-[100%] flex flex-col items-center gap-5">
			<div>
				<Input
					placeholder="search for lobbyID"
					value={searchedLobbyID}
					onChange={(e) => setSearchedLobbyID(e.target.value)}
				/>
			</div>
			<Card className="w-1/3 h-[360px] overflow-y-scroll">
				<Table>
					<TableBody>
						{lobbies
							.filter((lobby) =>
								new RegExp(searchedLobbyID, "i").test("" + lobby.id),
							)
							.map((lobby, index) => (
								<TableRow key={"lobby-row-" + index}>
									<TableCell className="font-medium">
										LobbyID: {lobby.id}
									</TableCell>
									<TableCell className="text-right">
										{lobby.playersJoined.length}/{MAX_PLAYERS} Players
									</TableCell>
									<TableCell className="text-right w-[50px]">
										{getStyledButtonForLobby(lobby)}
									</TableCell>
								</TableRow>
							))}
					</TableBody>
				</Table>
			</Card>
		</div>
	);
}
