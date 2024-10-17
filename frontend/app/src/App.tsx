import "./App.css";
import RemoveBeforeProdMvpUserTester from "@/components/tempHelpersForTestingManually/RemoveBeforeProdMvpUserTester.tsx";
import LoadingSpinner from "@/components/loadingSpinner/LoadingSpinner.tsx";
import { Link } from "react-router-dom";
import BasePage from "@/pages/basePage/BasePage.tsx";
import ProgrammInput from "./components/mainContent/playerInput/ProgramInput";

function App() {
	return (
		<BasePage>
			<div>hello world</div>
			<RemoveBeforeProdMvpUserTester></RemoveBeforeProdMvpUserTester>
			<LoadingSpinner></LoadingSpinner>
			<Link to={"/demo-route"}>demo route</Link>
			<ProgrammInput onProgramUploadClicked={(s) => console.log(s)} />
		</BasePage>
	);
}

export default App;
