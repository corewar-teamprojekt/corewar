import JsonDisplay from "@/components/jsonDisplay/JsonDisplay";
import { RequireUser } from "@/components/requireUser.tsx/RequireUser";
import { Button } from "@/components/ui/button";
import { getStatusV0 } from "@/services/rest/RestService";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function ResultDisplayPage() {
	const [result, setResult] = useState(null);
	const navigate = useNavigate();

	useEffect(() => {
		getStatusV0().then((response) => {
			if (response.ok) {
				response.json().then((data) => {
					setResult(data);
				});
			}
		});
	}, []);

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
