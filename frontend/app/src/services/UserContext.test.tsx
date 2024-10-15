import { describe, expect, it } from "vitest";
import {
	useDispatchUser,
	UserProvider,
	useUser,
} from "@/services/UserContext.tsx";
import { createRoot } from "react-dom/client";
import { screen } from "@testing-library/react";
import { act } from "react";

describe("mvp", () => {
	it("initializes as PlayerA", () => {
		const container = document.body.appendChild(document.createElement("div"));
		const root = createRoot(container!);
		act(() => {
			root.render(
				<UserProvider>
					<TestComponent></TestComponent>
				</UserProvider>,
			);
		});

		const playerName = screen.getByTestId("playerName").textContent;
		expect(playerName).toEqual("PlayerA");
	});
});

function TestComponent() {
	const user = useUser();
	const userDispatch = useDispatchUser();

	return (
		<>
			<div data-testid="playerName">{user?.name}</div>
			<button
				onClick={() => {
					if (userDispatch) {
						userDispatch({ user: null, type: "setPlayerA" });
					}
				}}
			>
				Set to PlayerA
			</button>
			<button
				onClick={() => {
					if (userDispatch) {
						userDispatch({ user: null, type: "setPlayerB" });
					}
				}}
			>
				Set to PlayerA
			</button>
		</>
	);
}
