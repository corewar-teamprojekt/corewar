import { useFrame } from "@react-three/fiber";
import Text from "@/components/three/Text.tsx";
import { useRef } from "react";
import * as THREE from "three";
import Electron from "@/components/three/Electron.tsx";

function Signature() {
	const ref = useRef<THREE.Mesh>(null!);

	const frameCounter = useRef(0);
	useFrame((_state, delta) => {
		_state.gl.setSize(600, 600);
		frameCounter.current += delta;

		const speed = 2;
		const baseRotation = frameCounter.current * speed;
		const easing = Math.sin((baseRotation % (2 * Math.PI)) + Math.PI);
		ref.current.rotation.y = baseRotation + easing * 0.5;
	});

	return (
		<mesh ref={ref}>
			<ambientLight intensity={0.5} />
			<directionalLight position={[10, 10, 10]} />
			<Text>COREWAR</Text>
			<Electron torusProps={[6, 0.005, 12, 48]} rotation={[0, 0, 0]} />
			<Electron torusProps={[6, 0.005, 12, 48]} rotation={[1, 0, 1]} />
			<Electron
				torusProps={[6, 0.005, 12, 48]}
				rotation={[0, Math.PI / 2, 0]}
			/>
			<Electron
				torusProps={[6, 0.005, 12, 48]}
				rotation={[0, 0, Math.PI / 2]}
			/>
			<Electron torusProps={[6, 0.005, 12, 48]} rotation={[1, Math.PI, 0]} />
			<Electron torusProps={[6, 0.005, 12, 48]} rotation={[0, 2, 1]} />
			<Electron torusProps={[6, 0.005, 12, 48]} rotation={[-1, 3, 0]} />
			<Electron torusProps={[6, 0.005, 12, 48]} rotation={[0, -1, 2]} />
		</mesh>
	);
}

export default Signature;
