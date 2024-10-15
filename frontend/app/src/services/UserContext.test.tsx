import { beforeEach, describe, expect, it } from "vitest";
import {
	useDispatchUser,
	UserProvider,
	useUser,
} from "@/services/UserContext.tsx";
import { cleanup, render, screen } from "@testing-library/react";
import { act } from "react";

describe("mvp", () => {
	beforeEach(() => {
		cleanup();
	});

	it("initializes as PlayerA", () => {
		act(() => {
			render(
				<UserProvider>
					<TestComponent></TestComponent>
				</UserProvider>,
			);
		});

		const playerName = screen.getByTestId("playerName").textContent;
		expect(playerName).toEqual("PlayerA");
	});

	it("can switch to PlayerB", () => {
		act(() => {
			render(
				<UserProvider>
					<TestComponent></TestComponent>
				</UserProvider>,
			);
		});

		act(() => {
			screen.getByTestId("switchToB").click();
		});

		const playerName = screen.getByTestId("playerName").textContent;
		expect(playerName).toEqual("PlayerB");
	});
});

function TestComponent() {
	const user = useUser();
	const userDispatch = useDispatchUser();

	return (
		<>
			<div data-testid="playerName">{user?.name}</div>
			<button
				data-testid="switchToA"
				onClick={() => {
					if (userDispatch) {
						userDispatch({ user: null, type: "setPlayerA" });
					}
				}}
			>
				Set to PlayerA
			</button>
			<button
				data-testid="switchToB"
				onClick={() => {
					if (userDispatch) {
						userDispatch({ user: null, type: "setPlayerB" });
					}
				}}
			>
				Set to PlayerB
			</button>
		</>
	);
}
