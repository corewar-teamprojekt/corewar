// This is just some threejs design magic, which is pretty much only for the looks and not important to properly type
// Plus its very unergonomic
//eslint-disable-next-line
function Torus({
	torusProps,
	rotation,
}: {
	torusProps: any;
	rotation: [number, number, number];
}) {
	return (
		<>
			<mesh rotation={rotation}>
				<torusGeometry args={torusProps} />
				<meshBasicMaterial color={"white"} />
			</mesh>
		</>
	);
}

export default Torus;
