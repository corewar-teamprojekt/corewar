import "./Header.css";
import { User } from "@/domain/user.ts";
import { useUser } from "@/services/userContext/UserContextHelpers.ts";
import { useLocation } from "react-router-dom";

function Header() {
	const user: User | null = useUser();
	const location = useLocation();

	return (
		<div id="headerContainer">
			<div id="headerText">
				<h2 className="text-3xl font-semibold">
					{location.pathname.length !== 1 && "Corewar"}
				</h2>
			</div>
			<div id="player">
				{user != null && (
					<>
						<svg
							width="36"
							height="36"
							id="playerIcon"
							role="img"
							aria-label="A colored circle"
						>
							<circle cx="18" cy="18" r="18" fill={user?.colorHex || "red"} />
						</svg>
						<small
							className="text-sm font-semibold leading-none"
							id="playerName"
						>
							{user?.name}
						</small>
					</>
				)}
			</div>
		</div>
	);
}

export default Header;
