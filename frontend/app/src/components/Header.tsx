import "./Header.css";
import { useUser } from "@/services/UserContext.tsx";
import { User } from "@/domain/user.ts";

function Header() {
	const user: User | null = useUser();

	return (
		<div id="headerContainer">
			<div id="headerText">
				<h2 className="text-3xl font-semibold">Corewar</h2>
			</div>
			<div id="player">
				<div id="playerIcon" style={{ backgroundColor: user?.colorHex }}></div>
				<small className="text-sm font-semibold leading-none" id="playerName">
					{user?.name}
				</small>
			</div>
		</div>
	);
}

export default Header;
