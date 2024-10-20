import Torus from "@/components/three/Torus.tsx";
import { Sphere } from "@react-three/drei";
import { useRef } from "react";
import * as THREE from "three";
import { useFrame } from "@react-three/fiber";

function Electron({
	torusProps,
	rotation,
}: {
	torusProps: unknown;
	rotation: [number, number, number];
}) {
	const ref = useRef<THREE.Mesh>(null!);
	let frameCount = (Math.random() - 1) * 60;
	const speed = Math.random() * 2; // Adjust this to control the speed of movement
	useFrame((_state, delta) => {
		frameCount += delta;
		// Adjust speed by multiplying with frameCount and delta

		// Moving in X-Y plane with a fixed Z distance
		const radius = 6; // The radius of the circular path

		// Default circular path on the X-Y plane
		const x = radius * Math.cos(frameCount * speed); // X component of the circle
		const y = radius * Math.sin(frameCount * speed); // Y component of the circle
		const z = 0; // Z is 0 because the circle starts in the X-Y plane

		// Extract rotation angles for X, Y, and Z
		const [rotX, rotY, rotZ] = rotation;

		// Compute rotation matrices for Z, Y, and X axes (in that order)
		const cosZ = Math.cos(rotZ),
			sinZ = Math.sin(rotZ);
		const cosY = Math.cos(rotY),
			sinY = Math.sin(rotY);
		const cosX = Math.cos(rotX),
			sinX = Math.sin(rotX);

		// First, apply rotation around Z-axis (affects X and Y)
		const x1 = x * cosZ - y * sinZ;
		const y1 = x * sinZ + y * cosZ;

		// Then, apply rotation around Y-axis (affects X1 and Z)
		const x2 = x1 * cosY + z * sinY;
		const z1 = -x1 * sinY + z * cosY;

		// Finally, apply rotation around X-axis (affects Y1 and Z1)
		const y2 = y1 * cosX - z1 * sinX;
		const z2 = y1 * sinX + z1 * cosX;

		// Set the position of the sphere after applying all rotations
		ref.current.position.set(x2, y2, z2);
	});

	return (
		<>
			<Torus torusProps={torusProps} rotation={rotation} />
			<Sphere ref={ref} position={[0, 6, 0]} scale={[0.2, 0.2, 0.2]}>
				<meshBasicMaterial color={"white"} />
			</Sphere>
		</>
	);
}

export default Electron;
