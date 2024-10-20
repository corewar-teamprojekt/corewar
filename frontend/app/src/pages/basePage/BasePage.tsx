import { ReactNode, useEffect, useState } from "react";
import Header from "@/components/header/Header.tsx";
import Footer from "@/components/footer/Footer.tsx";
import styles from "./BasePage.module.css";
import { Outlet } from "react-router-dom";

function GridCell({ isRed, isBlue }: { isRed: boolean; isBlue: boolean }) {
	return (
		<div
			className={`${styles.gridCell} ${isRed ? styles.redCell : ""} ${isBlue ? styles.blueCell : ""}`}
		></div>
	);
}

function BasePage() {
	const [activeRedCells, setActiveRedCells] = useState<number[]>([]);
	const [activeBlueCells, setActiveBlueCells] = useState<number[]>([]);
	const gridCells: ReactNode[] = [];
	for (let i = 0; i < 100; i++) {
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
			while (newActiveRedCells.length < 10) {
				const randomIndex = Math.floor(Math.random() * 100);
				if (!newActiveRedCells.includes(randomIndex)) {
					newActiveRedCells.push(randomIndex);
				}
			}
			setActiveRedCells(newActiveRedCells);

			const newActiveBlueCells: number[] = [];
			while (newActiveBlueCells.length < 10) {
				const randomIndex = Math.floor(Math.random() * 100);
				if (
					!newActiveBlueCells.includes(randomIndex) &&
					!newActiveRedCells.includes(randomIndex)
				) {
					newActiveBlueCells.push(randomIndex);
				}
			}
			setActiveBlueCells(newActiveBlueCells);
		}, 600);

		// Clear interval on component unmount
		return () => clearInterval(interval);
	}, []);

	return (
		<div id={styles["everything"]}>
			<div id={styles["background-animation"]}>
				<div id={styles["cellContainer"]}>{gridCells}</div>
			</div>
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
