import { describe, expect, it } from "vitest";
import { act, useRef, useState } from "react";
import { render, screen } from "@testing-library/react";
import HexagonalBoard from "@/components/hexagonalBoard/HexagonalBoard.tsx";
import { HexagonalTileProps } from "@/components/hexagonalTile/HexagonalTile.tsx";

declare global {
	interface Window {
		updateBoard: (newRows: number, newCols: number) => void;
	}
}

export {};

describe("hexagonalBoard layouting", () => {
	it("respects specified rows and columns", () => {
		let rows: number = 3;
		let cols: number = 5;
		act(() => {
			render(
				<HexBoardWrapper
					rows={rows}
					cols={cols}
					updateProps={null}
				></HexBoardWrapper>,
			);
		});

		expect(screen.getAllByText(/tile-0-.*/)).toHaveLength(cols);
		expect(screen.getAllByText(/tile-1-.*/)).toHaveLength(cols);
		expect(screen.getAllByText(/tile-2-.*/)).toHaveLength(cols);

		expect(screen.getAllByText(/tile-.*/)).toHaveLength(rows * cols);

		rows = 1;
		cols = 15;
		act(() => {
			updateBoard(rows, cols);
		});

		expect(screen.getAllByText(/tile-0-.*/)).toHaveLength(cols);

		expect(screen.getAllByText(/tile-.*/)).toHaveLength(rows * cols);
	});
});

describe("updateTile function", () => {
	it("sets the text of the correct tile", () => {
		const updatedTileX = 1;
		const updatedTileY = 3;
		const newTextContent = "someTestContent";

		const updatedTileProps = {
			updatedTileX: updatedTileX,
			updatedTileY: updatedTileY,
			updatedTileProps: {
				textContent: newTextContent,
			},
		};

		act(() => {
			render(
				<HexBoardWrapper
					rows={5}
					cols={5}
					updateProps={updatedTileProps}
				></HexBoardWrapper>,
			);
		});

		expect(
			screen
				.getByText(`tile-${updatedTileX}-${updatedTileY}`)
				?.parentElement?.querySelector("span")?.textContent,
		).toEqual("");
		act(() => {
			screen.getByTestId("call-update-tile").click();
		});
		expect(
			screen
				.getByText(`tile-${updatedTileX}-${updatedTileY}`)
				?.parentElement?.querySelector("span")?.textContent,
		).toEqual(newTextContent);
	});
});

let updateBoard: (newRows: number, newCols: number) => void; // Scoped outside the test function

const HexBoardWrapper = ({
	rows,
	cols,
	updateProps,
}: {
	rows: number;
	cols: number;
	updateProps: {
		updatedTileX: number;
		updatedTileY: number;
		updatedTileProps: { textContent: string };
	} | null;
}) => {
	const [rowCount, setRowCount] = useState(rows);
	const [colCount, setColCount] = useState(cols);

	const boardRef = useRef<{
		updateTile: (
			row: number,
			col: number,
			props: Partial<HexagonalTileProps>,
		) => void;
	}>(null);

	// Mock function to simulate external state change
	updateBoard = (newRows: number, newCols: number) => {
		setRowCount(newRows);
		setColCount(newCols);
	};

	const callUpdateTile = () => {
		if (boardRef.current && updateProps != null) {
			boardRef.current.updateTile(
				updateProps.updatedTileX,
				updateProps.updatedTileY,
				updateProps.updatedTileProps,
			);
		}
	};

	return (
		<>
			<HexagonalBoard rows={rowCount} tilesPerRow={colCount} ref={boardRef} />
			<button data-testid={"call-update-tile"} onClick={callUpdateTile}>
				Change tile
			</button>
		</>
	);
};
