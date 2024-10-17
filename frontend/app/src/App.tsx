import "./App.css";
import Header from "./components/header/Header.tsx";
import { ThemeProvider } from "./components/ThemeProvider.tsx";
import { UserProvider } from "@/services/userContext/UserContext.tsx";
import RemoveBeforeProdMvpUserTester from "@/components/tempHelpersForTestingManually/RemoveBeforeProdMvpUserTester.tsx";
import LoadingSpinner from "@/components/loadingSpinner/LoadingSpinner.tsx";
import Footer from "@/components/footer/Footer.tsx";

function App() {
	return (
		<>
			<ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
				<UserProvider>
					<Header />
					<div>hello world</div>
					<RemoveBeforeProdMvpUserTester></RemoveBeforeProdMvpUserTester>
					<LoadingSpinner></LoadingSpinner>
					<Footer></Footer>
				</UserProvider>
			</ThemeProvider>
		</>
	);
}

export default App;
