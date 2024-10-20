import { Button } from "@/components/ui/button.tsx";
import { useNavigate } from "react-router-dom";

function PlayerSelection() {
	const navigate = useNavigate();

	return <Button onClick={() => navigate("/player-coding")}>Route</Button>;
}

export default PlayerSelection;
