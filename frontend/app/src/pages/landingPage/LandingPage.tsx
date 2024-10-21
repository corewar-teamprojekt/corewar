import styles from "./LandingPage.module.css";
import { Button } from "@/components/ui/button.tsx";
import { Canvas } from "@react-three/fiber";
import Signature from "@/components/three/signature.tsx";
import { useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";

function LandingPage({ enableThreeJs }: { enableThreeJs: boolean }) {
	const navigate = useNavigate();
	const containerRef = useRef<HTMLDivElement>();

	const [scrollPosition, setScrollPosition] = useState(0);
	const handleScroll = () => {
		const position = window.scrollY || document.documentElement.scrollTop;
		setScrollPosition(position);
	};
	useEffect(() => {
		window.addEventListener("scroll", handleScroll);

		// Cleanup listener on component unmount
		return () => {
			window.removeEventListener("scroll", handleScroll);
		};
	}, []);

	useEffect(() => {
		if (containerRef.current)
			containerRef.current.style.transform = `
		translateY(clamp(-350px, -${0.38 * scrollPosition}px, 0px))
		translateX(clamp(-1200px, -${1.3 * scrollPosition}px, 0px))
		scale(clamp(0.3, ${1 - (scrollPosition * 0.85) / 1080}, 1)
		`;
	}, [scrollPosition]);

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
				<div id={styles["animationBlocker"]}></div>
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
			{enableThreeJs && (
				// just threejs styling
				// eslint-disable-next-line
				// @ts-ignore
				<div id={styles["initialAnimationContainer"]} ref={containerRef}>
					<Canvas camera={{ position: [0, 0, 100], zoom: 10 }}>
						<Signature />
					</Canvas>
				</div>
			)}
		</>
	);
}

export default LandingPage;
