function Torus({
	torusProps,
	rotation,
}: {
	torusProps: unknown;
	rotation: [number, number, number];
}) {
	return (
		<>
			<mesh rotation={rotation}>
				{
					// eslint-disable-next-line
					// @ts-ignore
					<torusGeometry args={torusProps} />
				}
				<meshBasicMaterial color={"white"} />
			</mesh>
		</>
	);
}

export default Torus;
