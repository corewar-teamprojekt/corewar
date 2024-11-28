import {
	useState,
	ReactNode,
	useEffect,
	forwardRef,
	useImperativeHandle,
	ForwardedRef,
} from "react";
import HexagonalTile, {
	HexagonalTileProps,
} from "@/components/hexagonalTile/HexagonalTile.tsx";
import "./HexagonalBoard.css";

const defaultTileProps: HexagonalTileProps = {
	fill: "",
	isDimmed: false,
	stroke: "gray",
	strokeWidth: "16",
	identifier: "",
	textContent: "",
};

interface HexagonalBoardProps {
	rows: number;
	tilesPerRow: number;
}

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
	const [tileProps, setTileProps] = useState<Map<string, HexagonalTileProps>>(
		new Map(),
	);

	useEffect(() => {
		const initialTileProps = new Map<string, HexagonalTileProps>();
		for (let i = 0; i < rows; i++) {
			for (let j = 0; j < tilesPerRow; j++) {
				const tileId = `tile-${i}-${j}`;
				initialTileProps.set(tileId, {
					...defaultTileProps,
					identifier: tileId,
				});
			}
		}
		setTileProps(initialTileProps);
	}, [rows, tilesPerRow]);

	// Update a specific tile's properties
	const updateTile = (
		row: number,
		col: number,
		props: Partial<HexagonalTileProps>,
	) => {
		const tileId = `tile-${row}-${col}`;
		setTileProps((prev) => {
			const updated = new Map(prev);
			const existingProps = updated.get(tileId) || {
				fill: "",
				isDimmed: false,
				stroke: "gray",
				strokeWidth: "16",
				identifier: tileId,
				textContent: "",
			};
			updated.set(tileId, { ...existingProps, ...props });
			return updated;
		});
	};

	// Expose `updateTile` via ref
	useImperativeHandle(ref, () => ({
		updateTile,
	}));

	const gridCellsMatrix: ReactNode[] = [];
	for (let i = 0; i < rows; i++) {
		const hexTiles = [];
		for (let j = 0; j < tilesPerRow; j++) {
			const tileId = `tile-${i}-${j}`;
			hexTiles.push(
				<div className="layoutedHex" key={tileId}>
					<HexagonalTile
						{...{ ...defaultTileProps, ...(tileProps.get(tileId) ?? {}) }}
					/>
				</div>,
			);
		}
		gridCellsMatrix.push(
			<div
				className="boardRow"
				key={`row-${i}`}
				style={{
					marginLeft: i % 2 === 0 ? "0px" : "140px",
					marginTop: i === 0 ? "0px" : "-70px",
				}}
			>
				{hexTiles}
			</div>,
		);
	}

	return <div className="board">{gridCellsMatrix}</div>;
});

export default HexagonalBoard;
