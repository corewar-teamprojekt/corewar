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
	const frameCount = useRef(0);
	const speed = 2; // Adjust this to control the speed of movement
	useFrame((_state, delta) => {
		frameCount.current += delta;
		// Adjust speed by multiplying with frameCount and delta

		// Moving in X-Y plane with a fixed Z distance
		const radius = 6; // The radius of the circular path

		// Default circular path on the X-Y plane
		const internalX = radius * Math.cos(frameCount.current * speed); // X component of the circle
		const internalY = radius * Math.sin(frameCount.current * speed); // Y component of the circle
		const internalZ = 0; // Z is 0 because the circle starts in the X-Y plane

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
		const x1 = internalX * cosZ - internalY * sinZ;
		const y1 = internalX * sinZ + internalY * cosZ;

		// Then, apply rotation around Y-axis (affects X1 and Z)
		ref.current.position.x = x1 * cosY + internalZ * sinY;
		const z1 = -x1 * sinY + internalZ * cosY;

		// Finally, apply rotation around X-axis (affects Y1 and Z1)
		ref.current.position.y = y1 * cosX - z1 * sinX;
		ref.current.position.z = y1 * sinX + z1 * cosX;

		// Set the position of the sphere after applying all rotations
		ref.current.position.set(
			ref.current.position.x,
			ref.current.position.y,
			ref.current.position.z,
		);
	});

	return (
		<>
			<Torus torusProps={torusProps} rotation={rotation} />
			<Sphere ref={ref} scale={[0.2, 0.2, 0.2]}>
				<meshBasicMaterial color={"white"} />
			</Sphere>
		</>
	);
}

export default Electron;
