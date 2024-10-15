import "./App.css";
import Header from "./components/Header.tsx";
import { ThemeProvider } from "./components/ThemeProvider.tsx";

function App() {
	return (
		<ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
			<Header />
			<div>hello world</div>
		</ThemeProvider>
	);
}

export default App;
