import roboto from "../../assets/Roboto Mono_Regular.json";
import { Center, Text3D } from "@react-three/drei";
import { ReactNode } from "react";

function Text({ children }: { children: ReactNode }) {
	return (
		<mesh>
			<Center>
				<Text3D
					curveSegments={32}
					bevelEnabled
					bevelSize={0.04}
					bevelThickness={0.1}
					height={0.5}
					lineHeight={0.5}
					letterSpacing={-0.06}
					size={1.5}
					font={roboto as unknown as string}
				>
					{children}
					<meshBasicMaterial wireframe />
				</Text3D>
			</Center>
		</mesh>
	);
}

export default Text;
