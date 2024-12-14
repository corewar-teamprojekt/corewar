import { beforeEach, describe, expect, it } from "vitest";
import { UserProvider } from "@/services/userContext/UserContext.tsx";
import { cleanup, render, screen } from "@testing-library/react";
import { act } from "react";
import { User } from "@/domain/User.ts";
import {
	useDispatchUser,
	userReducer,
	useUser,
} from "@/services/userContext/UserContextHelpers.ts";

describe("mvp", () => {
	beforeEach(() => {
		cleanup();
	});

	describe("reducer", () => {
		it("returns playerA on setPlayerA", () => {
			expect(userReducer(null, { type: "setPlayerA", user: null })).toEqual(
				new User("playerA", "#FF006E"),
			);
		});

		it("returns playerB on setPlayerB", () => {
			expect(userReducer(null, { type: "setPlayerB", user: null })).toEqual(
				new User("playerB", "#00FFFF"),
			);
		});

		it("returns playerB after initializing with playerA and then setting to playerB", () => {
			const playerA = userReducer(null, { type: "setPlayerA", user: null });
			expect(userReducer(playerA, { type: "setPlayerB", user: null })).toEqual(
				new User("playerB", "#00FFFF"),
			);
		});
	});

	describe("context", () => {
		it("initializes as null", () => {
			act(() => {
				render(
					<UserProvider>
						<TestComponent></TestComponent>
					</UserProvider>,
				);
			});

			const playerName = screen.getByTestId("playerName").textContent;
			expect(playerName).toBeFalsy();
		});

		it("can switch to PlayerA", () => {
			act(() => {
				render(
					<UserProvider>
						<TestComponent></TestComponent>
					</UserProvider>,
				);
			});

			act(() => {
				screen.getByTestId("switchToA").click();
			});

			const playerName = screen.getByTestId("playerName").textContent;
			expect(playerName).toEqual("playerA");
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
			expect(playerName).toEqual("playerB");
		});
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
