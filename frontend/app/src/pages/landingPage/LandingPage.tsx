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
		<RequireLogout blocked={false}>
			<div
				className={styles["fullContentView"]}
				id={styles["initialContentView"]}
			>
				<div id={styles["initialBigTextContainer"]}>
					<h1 className="text-9xl font-extrabold">Corewar</h1>
					<h2 className="text-3xl font-semibold">
						Competitively optimizing assembly code
					</h2>
					<Button onClick={() => navigate("/lobby-selection")}>
						<h2 className="text-2xl font-semibold">Play</h2>
					</Button>
				</div>
				<div id={styles["animationBlocker"]}></div>
			</div>
			<div className={styles["fullContentView"]}>
				<div id={styles["infodump0Layout"]}>
					<Card className={styles["textCard"]}>
						<CardContent>
							<h2 className="text-3xl font-semibold">Welcome to CoreWar!</h2>
							CoreWar is a strategic programming game originally developed in
							1984 by A. K. Dewdney and D. G. Jones. In it, players write
							programs in an assembly-esque programming language that compete
							within a shared memory space. The goal is to get the opponent to
							execute an illegal instruction, at which point the program gets
							killed.
							<br />
							This implementation was developed as part of a university course
							in cooperation with ConLeos GmbH.
							<Button
								onClick={() =>
									window.open("https://github.com/corewar-teamprojekt/corewar")
								}
							>
								Check out the Code!
							</Button>
						</CardContent>
					</Card>
				</div>
			</div>
			<Card className={styles["cheatSheet"]}>
				<h2 className="text-3xl font-semibold">Cheat Sheet</h2>
				<div id={styles["infodump1Layout"]}>
					<div id={styles["infodump1GridItem0"]}>
						<h2 className="text-2xl font-semibold">Memory Layout</h2>
						CoreWar uses a circular memory layout. That means reads and writes
						past the size of the memory will loop around to the start or the end
						again. Addressing is always relative to the current instruction. If
						an instruction references address 2, it points at the instruction
						two steps ahead of it. If address -2 is referenced, it means the
						instruction 2 steps before it in memory.
					</div>
					<div id={styles["infodump1GridItem1"]}>
						<h2 className="text-2xl font-semibold">Instruction Set</h2>
						- DAT: Kills the executing process
						<br />
						- MOV: Copies data from source to destination
						<br />
						- ADD: Adds source to destination
						<br />
						- SUB: Subtracts source from destination
						<br />
						- MUL: Multiplies source to destination
						<br />
						- DIV: Divides source by destination
						<br />
						- MOD: Calculates the remainder after division of source by
						destination
						<br />
						- JMP: Jump to a destination pointed at by the source
						<br />
						- JMZ: Jump to the source when the destination is zero
						<br />
						- JMN: Jump to the source when the destination is not zero
						<br />
						- SEQ: Skip next instruction if source and destination are equal
						<br />
						- SNE: Skip next instruction if source and destination are not equal
						<br />
						- SLT: Skip next instruction if source is less than destination
						<br />
						- DJN: Decrement the source by one, then jump if it is not zero
						<br />
						- SPL: Create a new process at the source instruction
						<br />
						- NOP: Do nothing
						<br />
					</div>
					<div id={styles["infodump1GridItem2"]}>
						<h2 className="text-2xl font-semibold">Address Modes</h2>
						- '#': Immediate mode
						<br />
						- '$': Direct mode
						<br />
						- '*': A-field indirect
						<br />
						- '@': B-field indirect
						<br />
						- '&#123;': A-field indirect with predecrement
						<br />
						- '&lt;': B-field indirect with predecrement
						<br />
						- '&#125;': A-field indirect with postincrement
						<br />- '&gt;': B-field indirect with postincrement
					</div>
					<div id={styles["infodump1GridItem3"]}>
						<h2 className="text-2xl font-semibold">Modifiers</h2>
						- A: Operates on the A-field of both source and destination
						<br />
						- B: Operates on the B-field of both source and destination
						<br />
						- AB: Operates on the A-field of the source and the B-field of the
						destination
						<br />
						- BA: Operates on the B-field of the source and the A-field of the
						destination
						<br />
						- F: Does the operation like the A modifier, then like the
						B-modifier
						<br />
						- X: Does the operation like the AB modifier, then like the
						BA-modifier
						<br />
						- I: Operates on the whole instruction (including opcode)
						<br />
					</div>
				</div>
			</Card>
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
