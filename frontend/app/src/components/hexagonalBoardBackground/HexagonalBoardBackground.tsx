import { useRef, useEffect, MutableRefObject } from "react";
import HexagonalBoard from "@/components/hexagonalBoard/HexagonalBoard.tsx";
import { HexagonalTileProps } from "@/components/hexagonalTile/HexagonalTile.tsx";
import "./HexagonalBoardBackground.css";

// This is just our "funny background" component. I think there is no real benefit in writing tests for this and
// if we would want to write tests, it feel it would be very difficult. This is mostly visual design stuff.
function HexagonalBoardBackground() {
	function turnOffCells(cells: MutableRefObject<{ x: number; y: number }[]>) {
		if (!boardRef.current) {
			console.error("Board ref is undefined!");
			return;
		}

		for (const cell of cells.current) {
			boardRef.current.updateTile(cell.y, cell.x, {
				fill: "",
				isDimmed: false,
				stroke: "gray",
				strokeWidth: "16",
				textContent: "",
			});
		}

		cells.current = [];
	}

	function turnOnRandomCellToColor(
		listOfActiveCells: MutableRefObject<{ x: number; y: number }[]>,
		color: string,
	) {
		if (!boardRef.current) {
			console.error("Board ref is undefined!");
			return;
		}

		let randomCoords: { x: number; y: number };

		do {
			randomCoords = {
				x: Math.floor(Math.random() * COLS),
				y: Math.floor(Math.random() * ROWS),
			};
		} while (listOfActiveCells.current.includes(randomCoords));

		boardRef.current.updateTile(randomCoords.y, randomCoords.x, {
			fill: color,
			isDimmed: false,
			stroke: color,
			strokeWidth: "16",
			textContent: "",
		});
		listOfActiveCells.current.push(randomCoords);
	}

	const ROWS = 10;
	const COLS = 10;

	const BACKGROUND_BLINKING_INTERVAL_MS: number = 1200;
	const RED_CELL_COUNT: number = 15;
	const BLUE_CELL_COUNT: number = 15;

	const activeCells = useRef<{ x: number; y: number }[]>([]);

	const boardRef = useRef<{
		updateTile: (
			row: number,
			col: number,
			props: Partial<HexagonalTileProps>,
		) => void;
	}>(null);

	useEffect(() => {
		const interval = setInterval(() => {
			if (!boardRef.current) {
				return;
			}

			turnOffCells(activeCells);

			for (let i: number = 0; i < RED_CELL_COUNT; i++) {
				turnOnRandomCellToColor(activeCells, "#FF006E");
			}

			for (let i: number = 0; i < BLUE_CELL_COUNT; i++) {
				turnOnRandomCellToColor(activeCells, "#00FFFF");
			}
		}, BACKGROUND_BLINKING_INTERVAL_MS);

		// Clear interval on component unmount
		return () => clearInterval(interval);
	}, []);

	return (
		<div className={"background-animation"}>
			<HexagonalBoard rows={ROWS} tilesPerRow={COLS} ref={boardRef} />
		</div>
	);
}

export default HexagonalBoardBackground;
