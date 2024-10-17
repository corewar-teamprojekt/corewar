import { ReactNode } from "react";
import Header from "@/components/header/Header.tsx";
import Footer from "@/components/footer/Footer.tsx";
import styles from "./BasePage.module.css";

function BasePage({ children }: { children: ReactNode }) {
	return (
		<>
			<div id={styles["everything"]}>
				<Header />
				<div id={styles["mainContent"]}>{children}</div>
				<Footer />
			</div>
		</>
	);
}

export default BasePage;
