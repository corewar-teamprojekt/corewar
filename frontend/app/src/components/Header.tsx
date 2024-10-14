import "./Header.css";

function Header() {
	return (
		<div id="headerContainer">
			<div id="headerText">
				<h2 className="text-3xl font-semibold">Corewar</h2>
			</div>
			<div id="player">
				<div id="playerIcon"></div>
				<small className="text-sm font-semibold leading-none" id="playerName">
					Player
				</small>
			</div>
		</div>
	);
}

export default Header;
