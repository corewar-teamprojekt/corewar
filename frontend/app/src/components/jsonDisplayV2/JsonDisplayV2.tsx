import styles from "./JsonDisplayV2.module.css";

// expects object, will convert to string on its own
export default function JsonDisplayV2({ json }: Readonly<{ json: unknown }>) {
	function formatJson(json: unknown): string {
		const jsonString = JSON.stringify(json, null, 2);
		return jsonString.replace(/[{}",]/g, "");
	}

	return (
		<div className={styles["jsonDisplayV2Container"]}>
			<pre>{formatJson(json)}</pre>
		</div>
	);
}
