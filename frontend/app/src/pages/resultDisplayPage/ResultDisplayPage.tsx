import JsonDisplay from "@/components/jsonDisplay/JsonDisplay";
import { RequireUser } from "@/components/requireUser.tsx/RequireUser";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getLobbyStatusV1 } from "@/services/rest/LobbyRest.ts";
import { useLobby } from "@/services/lobbyContext/LobbyContextHelpers.ts";
import { LobbyStatus } from "@/domain/LobbyStatus.ts";

export default function ResultDisplayPage() {
	const [result, setResult] = useState<LobbyStatus | null>(null);
	const navigate = useNavigate();
	const lobby = useLobby();

	useEffect(() => {
		if (!lobby) {
			console.error("Lobby is undefined");
			return;
		}
		getLobbyStatusV1(lobby.id).then((response) => setResult(response));
	}, [lobby]);

	return (
		<RequireUser>
			<div className="flex flex-col justify-center items-center h-[100%] w-[100%] gap-10">
				<h2 className="text-3xl font-semibold">Result: Player A vs Player B</h2>
				<JsonDisplay json={result} />
				<Button onClick={() => navigate("/player-selection")}>
					Play again
				</Button>
			</div>
		</RequireUser>
	);
}
