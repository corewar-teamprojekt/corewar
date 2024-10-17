import "./App.css";
import RemoveBeforeProdMvpUserTester from "@/components/tempHelpersForTestingManually/RemoveBeforeProdMvpUserTester.tsx";
import LoadingSpinner from "@/components/loadingSpinner/LoadingSpinner.tsx";
import { Link } from "react-router-dom";
import BasePage from "@/pages/basePage/BasePage.tsx";

function App() {
	return (
		<>
			<BasePage>
				<div>hello world</div>
				<RemoveBeforeProdMvpUserTester></RemoveBeforeProdMvpUserTester>
				<LoadingSpinner></LoadingSpinner>
				<Link to={"/demo-route"}>demo route</Link>
			</BasePage>
		</>
	);
}

export default App;
