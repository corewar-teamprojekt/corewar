import { ReactNode, useEffect, useState } from "react";
import Header from "@/components/header/Header.tsx";
import Footer from "@/components/footer/Footer.tsx";
import styles from "./BasePage.module.css";
import { Outlet } from "react-router-dom";
import HexagonalBoardBackground from "@/components/hexagonalBoardBackground/HexagonalBoardBackground.tsx";

function GridCell({ isRed, isBlue }: { isRed: boolean; isBlue: boolean }) {
	return (
		<div
			className={`${styles.gridCell} ${isRed ? styles.redCell : ""} ${isBlue ? styles.blueCell : ""}`}
		></div>
	);
}

function BasePage() {
	const BACKGROUND_BLINKING_INTERVAL_MS: number = 1200;
	const GRIDCELL_COUNT: number = 100;
	const PINK_CELL_COUNT: number = 10;
	const CYAN_CELL_COUNT: number = 10;

	const [activeRedCells, setActiveRedCells] = useState<number[]>([]);
	const [activeBlueCells, setActiveBlueCells] = useState<number[]>([]);
	const gridCells: ReactNode[] = [];
	for (let i = 0; i < GRIDCELL_COUNT; i++) {
		gridCells.push(
			<GridCell
				key={i}
				isRed={activeRedCells.includes(i)}
				isBlue={activeBlueCells.includes(i)}
			></GridCell>,
		);
	}

	useEffect(() => {
		const interval = setInterval(() => {
			const newActiveRedCells: number[] = [];
			while (newActiveRedCells.length < PINK_CELL_COUNT) {
				const randomIndex = Math.floor(Math.random() * GRIDCELL_COUNT);
				if (!newActiveRedCells.includes(randomIndex)) {
					newActiveRedCells.push(randomIndex);
				}
			}
			setActiveRedCells(newActiveRedCells);

			const newActiveBlueCells: number[] = [];
			while (newActiveBlueCells.length < CYAN_CELL_COUNT) {
				const randomIndex = Math.floor(Math.random() * GRIDCELL_COUNT);
				if (
					!newActiveBlueCells.includes(randomIndex) &&
					!newActiveRedCells.includes(randomIndex)
				) {
					newActiveBlueCells.push(randomIndex);
				}
			}
			setActiveBlueCells(newActiveBlueCells);
		}, BACKGROUND_BLINKING_INTERVAL_MS);

		// Clear interval on component unmount
		return () => clearInterval(interval);
	}, []);

	return (
		<div id={styles["everything"]}>
			<HexagonalBoardBackground />
			<Header />
			<div id={styles["mainContent"]}>
				<div id={styles["layoutingContainer"]}>
					<Outlet />
				</div>
			</div>
			<Footer />
		</div>
	);
}

export default BasePage;
