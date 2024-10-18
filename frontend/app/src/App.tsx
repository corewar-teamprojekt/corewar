import "./App.css";
import RemoveBeforeProdMvpUserTester from "@/components/tempHelpersForTestingManually/RemoveBeforeProdMvpUserTester.tsx";
import { Link } from "react-router-dom";
import BasePage from "@/pages/basePage/BasePage.tsx";
import ProgrammInput from "./components/mainContent/playerInput/ProgramInput";
import JsonDisplay from "@/components/jsonDisplay/JsonDisplay.tsx";

function App() {
	const jsonObj: unknown = {
		root: {
			obj: 1,
			helo: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
		},
	};

	return (
		<BasePage>
			<JsonDisplay json={jsonObj} />
			<RemoveBeforeProdMvpUserTester></RemoveBeforeProdMvpUserTester>
			<Link to={"/demo-route"}>demo route</Link>
			<br />
			<Link to={"/waiting-for-result"}>Waiting for result</Link>
			<Link to={"/player-coding"}>Player coding page</Link>
			<ProgrammInput onProgramUploadClicked={(s) => console.log(s)} />
		</BasePage>
	);
}

export default App;
