import styles from "./LoadingSpinner.module.css";

function LoadingSpinner() {
	return (
		<img
			width="600"
			height="600"
			className={styles.loadingSpinner}
			alt="Loading spinner"
			src={"corewarIcon.svg"}
		></img>
	);
}

export default LoadingSpinner;
