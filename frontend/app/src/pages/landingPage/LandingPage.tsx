import BasePage from "@/pages/basePage/BasePage.tsx";
import styles from "./LandingPage.module.css";
import { Button } from "@/components/ui/button.tsx";

function LandingPage() {
	return (
		<BasePage>
			<div
				className={styles["fullContentView"]}
				id={styles["initialContentView"]}
			>
				<div id={styles["initialBigTextContainer"]}>
					<h1 className="text-9xl font-extrabold">Corewar</h1>
					<h2 className="text-3xl font-semibold">
						Competitively optimizing assembly code
					</h2>
					<Button>
						<h2 className="text-2xl font-semibold">Play</h2>
					</Button>
				</div>
				<div id={styles["initialAnimationContainer"]}></div>
			</div>
			<div className={styles["fullContentView"]}>
				<h3>Infodump 0</h3>
			</div>
			<div className={styles["fullContentView"]}>
				<h3>Infodump 1</h3>
			</div>
			<div className={styles["fullContentView"]}>
				<h3>Infodump 2</h3>
			</div>
		</BasePage>
	);
}

export default LandingPage;
