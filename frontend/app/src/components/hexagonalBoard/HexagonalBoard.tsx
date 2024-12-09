import {
	useState,
	forwardRef,
	useImperativeHandle,
	ForwardedRef,
	memo,
	useMemo,
	useCallback,
} from "react";
import HexagonalTile, {
	HexagonalTileProps,
} from "@/components/hexagonalTile/HexagonalTile.tsx";
import "./HexagonalBoard.css";

interface HexagonalBoardProps {
	rows: number;
	tilesPerRow: number;
}

// Memoized row component to prevent unnecessary re-renders
const MemoizedRow = memo(
	({
		row,
		rowTiles,
	}: {
		row: number;
		tilesPerRow: number;
		rowTiles: HexagonalTileProps[];
	}) => {
		return (
			<div
				key={`row-${row}`}
				className={`boardRow ${row % 2 === 0 ? "even" : "odd"}`}
			>
				{rowTiles.map((tileData, col) => (
					<HexagonalTile key={`tile-${row}-${col}`} {...tileData} />
				))}
			</div>
		);
	},
	(prevProps, nextProps) => {
		// Only re-render if the tiles in this specific row have changed
		return prevProps.rowTiles === nextProps.rowTiles;
	},
);

const HexagonalBoard = forwardRef(function HexagonalBoard(
	{ rows, tilesPerRow }: HexagonalBoardProps,
	ref?: ForwardedRef<{
		updateTile: (
			row: number,
			col: number,
			props: Partial<HexagonalTileProps>,
		) => void;
	}>,
) {
	const defaultTileProps = useMemo(
		(): HexagonalTileProps => ({
			fill: "",
			isDimmed: false,
			stroke: "gray",
			strokeWidth: "16",
			identifier: "",
			textContent: "",
			textColor: "white",
		}),
		[],
	);

	const [tileProps, setTileProps] = useState<HexagonalTileProps[][]>(() =>
		Array.from({ length: rows }, (_, row) =>
			Array.from({ length: tilesPerRow }, (_, col) => ({
				...defaultTileProps,
				identifier: `tile-${row}-${col}`,
			})),
		),
	);

	const updateTile = useCallback(
		(row: number, col: number, props: Partial<HexagonalTileProps>) => {
			setTileProps((prev) => {
				// Minimize object creation
				const newTileProps = [...prev];
				newTileProps[row] = [...prev[row]];
				newTileProps[row][col] = {
					...prev[row][col],
					...props,
				};
				return newTileProps;
			});
		},
		[],
	);

	useImperativeHandle(ref, () => ({
		updateTile,
	}));

	return (
		<div className="board">
			{tileProps.map((rowTiles, row) => (
				<MemoizedRow
					key={`row-${row}`}
					row={row}
					tilesPerRow={tilesPerRow}
					rowTiles={rowTiles}
				/>
			))}
		</div>
	);
});

export default HexagonalBoard;
