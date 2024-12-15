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
		updateTiles: (
			updates: {
				row: number;
				col: number;
				props: Partial<HexagonalTileProps>;
			}[],
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

	const updateTiles = useCallback(
		(
			updates: {
				row: number;
				col: number;
				props: Partial<HexagonalTileProps>;
			}[],
		) => {
			setTileProps((prev) => {
				// Use a Map to track rows we need to clone
				const modifiedRows = new Map<number, HexagonalTileProps[]>();

				updates.forEach(({ row, col, props }) => {
					// Clone row if not already cloned
					if (!modifiedRows.has(row)) {
						modifiedRows.set(row, [...prev[row]]);
					}

					// Update the tile in the cloned row
					const clonedRow = modifiedRows.get(row)!;
					clonedRow[col] = {
						...clonedRow[col],
						...props,
					};
				});

				// Create the final updated state
				const newTileProps = [...prev];
				modifiedRows.forEach((row, rowIndex) => {
					newTileProps[rowIndex] = row;
				});

				return newTileProps;
			});
		},
		[],
	);

	useImperativeHandle(ref, () => ({
		updateTiles,
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
