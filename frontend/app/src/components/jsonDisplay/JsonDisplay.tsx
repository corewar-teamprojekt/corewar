import styles from "./JsonDisplay.module.css";

// expects object, will convert to string on its own
function JsonDisplay({ json }: { json: unknown }) {
	return (
		<div className={styles["jsonDisplayContainer"]}>
			<pre>{JSON.stringify(json, null, 2)}</pre>
		</div>
	);
}

export default JsonDisplay;
