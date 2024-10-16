import styles from "./LoadingSpinner.module.css";

function LoadingSpinner() {
	return (
		<svg
			width="300"
			height="300"
			viewBox="0 0 300 300"
			xmlns="http://www.w3.org/2000/svg"
			className={styles.loadingSpinner}
		>
			<path
				d="
                M 150,10
                A 140,140 0 1,1 10,150
            "
				stroke="white"
				strokeWidth="20"
				fill="none"
			/>
		</svg>
	);
}

export default LoadingSpinner;
