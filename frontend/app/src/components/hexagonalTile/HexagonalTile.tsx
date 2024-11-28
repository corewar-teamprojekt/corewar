import "./HexagonalTile.css";

export interface HexagonalTileProps {
	fill: string;
	isDimmed: boolean;
	stroke: string;
	strokeWidth: string;
	identifier: string;
	textContent: string;
}

// I see no point in adding tests for this component. This is pretty much just forwarding the props.
function HexagonalTile({
	fill = "",
	isDimmed = false,
	strokeWidth = "1",
	stroke = "gray",
	identifier = "",
	textContent = "",
}: Readonly<HexagonalTileProps>) {
	let fillColor = fill;

	if (fill != "") {
		const alpha = isDimmed ? "88" : "FF";
		fillColor = fillColor + alpha;
	}

	return (
		<div style={{ position: "relative" }}>
			<svg
				width="278"
				height="330"
				viewBox="0 0 278 302"
				fill="none"
				xmlns="http://www.w3.org/2000/svg"
			>
				<path
					d="M117.316 1.6477C130.165 -5.24424 148.318 -5.2374 161.167 1.677L252.195 55.2083C265.043 62.1227 273.362 75.3227 273.375 91.377L273.505 202.007C273.518 218.062 265.212 231.256 252.363 238.148L161.296 291.669C148.447 298.561 130.293 298.548 117.444 291.634L26.416 238.102C13.5671 231.188 5.24892 217.988 5.23604 201.934L5.10583 91.304C5.09296 75.2489 13.3989 62.0547 26.2479 55.1628L117.316 1.6477Z"
					fill={fillColor}
					stroke={stroke}
					strokeWidth={strokeWidth}
				/>
				{identifier}
			</svg>
			<span className={"textContent"}>{textContent}</span>
		</div>
	);
}

export default HexagonalTile;
