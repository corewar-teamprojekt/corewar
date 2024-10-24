import { RequireLogout } from "@/components/requireLogout/RequireLogout";
import Signature from "@/components/three/signature.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Card, CardContent } from "@/components/ui/card.tsx";
import { Canvas } from "@react-three/fiber";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./LandingPage.module.css";

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
		translateY(clamp(-350px, -${1.11 * 0.38 * scrollPosition}px, 0px))
		translateX(clamp(-1200px, -${1.11 * 1.3 * scrollPosition}px, 0px))
		scale(clamp(0.3, ${1 - (1.11 * scrollPosition * 0.85) / 1080}, 1)
		`;
	}, [scrollPosition]);

	return (
		<RequireLogout>
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
				<div id={styles["infodump0Layout"]}>
					<Card className={styles["textCard"]}>
						<CardContent>
							<h2 className="text-3xl font-semibold">Hello World</h2>
							<p>
								Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
								eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut
								enim ad minim veniam, quis nostrud exercitation ullamco laboris
								nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor
								in reprehenderit in voluptate velit esse cillum dolore eu fugiat
								nulla pariatur. Excepteur sint occaecat cupidatat non proident,
								sunt in culpa qui officia deserunt mollit anim id est laborum.
							</p>
							<Button>READ MORE</Button>
						</CardContent>
					</Card>
					<div id={styles["poster"]}></div>
				</div>
			</div>
			<div id={styles["lastFullContentView"]}>
				<div id={styles["infodump1Layout"]}>
					<div id={styles["infodump1GridItem0"]}>abc</div>
					<div id={styles["infodump1GridItem1"]}>def</div>
					<div id={styles["infodump1GridItem2"]}>ghi</div>
					<div id={styles["infodump1GridItem3"]}>abc</div>
				</div>
			</div>
			{enableThreeJs && (
				// just threejs styling
				// eslint-disable-next-line
				// @ts-ignore
				<div id={styles["initialAnimationContainer"]} ref={containerRef}>
					<Canvas camera={{ position: [0, 10, 100], zoom: 10 }}>
						<Signature />
					</Canvas>
				</div>
			)}
		</RequireLogout>
	);
}

export default LandingPage;
