import { Lobby } from "@/domain/Lobby";
import { Table, TableBody, TableCell, TableRow } from "../ui/table";
import { Button } from "../ui/button";
import { Card } from "../ui/card";
import { Input } from "../ui/input";
import { useState } from "react";

interface LobbySelectionProps {
	lobbies: Lobby[];
	joinLobby: (lobbyId: number) => void;
}

export default function LobbbySelection({
	lobbies,
	joinLobby,
}: Readonly<LobbySelectionProps>) {
	const [searchedLobbyID, setSearchedLobbyID] = useState("");

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
										{lobby.playersJoined.length}/2 Players
									</TableCell>
									<TableCell className="text-right w-[50px]">
										<Button
											onClick={() => joinLobby(lobby.id)}
											variant="outline"
										>
											Join
										</Button>
									</TableCell>
								</TableRow>
							))}
					</TableBody>
				</Table>
			</Card>
		</div>
	);
}
