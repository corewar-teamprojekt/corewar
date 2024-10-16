import "./Header.css";
import { useUser } from "@/services/userContext/UserContext.tsx";
import { User } from "@/domain/user.ts";

function Header() {
	const user: User | null = useUser();

	return (
		<div id="headerContainer">
			<div id="headerText">
				<h2 className="text-3xl font-semibold">Corewar</h2>
			</div>
			<div id="player">
				<svg
					width="36"
					height="36"
					id="playerIcon"
					role="img"
					aria-label="A colored circle"
				>
					<circle cx="18" cy="18" r="18" fill={user?.colorHex || "red"} />
				</svg>
				<small className="text-sm font-semibold leading-none" id="playerName">
					{user?.name}
				</small>
			</div>
		</div>
	);
}

export default Header;
