import "./App.css";
import Header from "./components/Header.tsx";
import { ThemeProvider } from "./components/ThemeProvider.tsx";
import { UserProvider } from "@/services/UserContext.tsx";
import Button from "@/components/ChangeUser.tsx";

function App() {
	return (
		<>
            <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
				<UserProvider>
					<Header />
					<div>hello world</div>
					<Button></Button>
				</UserProvider>
            </ThemeProvider>
        </>
	);
}

export default App;
