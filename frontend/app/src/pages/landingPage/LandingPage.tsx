import styles from "./LandingPage.module.css";
import { Button } from "@/components/ui/button.tsx";
import { Canvas } from "@react-three/fiber";
import Signature from "@/components/three/signature.tsx";
import { useNavigate } from "react-router-dom";

function LandingPage({ enableThreeJs }: { enableThreeJs: boolean }) {
	const navigate = useNavigate();

	return (
		<>
			<div
				className={styles["fullContentView"]}
				id={styles["initialContentView"]}
			>
				<div id={styles["initialBigTextContainer"]}>
					<h1 className="text-9xl font-extrabold">Corewar</h1>
					<h2 className="text-3xl font-semibold">
						Competitively optimizing assembly code
					</h2>
					<Button onClick={() => navigate("/player-selection")}>
						<h2 className="text-2xl font-semibold">Play</h2>
					</Button>
				</div>
				{enableThreeJs && (
					<div id={styles["initialAnimationContainer"]}>
						<Canvas camera={{ position: [0, 0, 100], zoom: 10 }}>
							<Signature />
						</Canvas>
					</div>
				)}
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
		</>
	);
}

export default LandingPage;
