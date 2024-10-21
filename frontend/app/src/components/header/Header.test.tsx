import { afterEach, beforeEach, describe, expect, it, Mock, vi } from "vitest";
import { act, useEffect } from "react";
import { UserProvider } from "@/services/userContext/UserContext.tsx";
import Header from "@/components/header/Header.tsx";
import { cleanup, render, screen } from "@testing-library/react";
import { useDispatchUser } from "@/services/userContext/UserContextHelpers.ts";
import { useLocation } from "react-router-dom";

vi.mock("react-router-dom");

beforeEach(() => {
	(useLocation as Mock).mockReturnValue({ pathname: "/" });
	cleanup();
});

afterEach(() => {
	vi.clearAllMocks();
});

describe("playerIndicator", () => {
	describe("doesn't display anything when user is null", () => {
		it("playerName", () => {
			act(() => {
				render(
					<UserProvider>
						<UserDispatcherInteractor dispatcherCommand={null} />
					</UserProvider>,
				);
			});

			expect(screen.queryByText("Player")).toBeFalsy();
		});

		it("playerColor", () => {
			act(() => {
				render(
					<UserProvider>
						<UserDispatcherInteractor dispatcherCommand={null} />
					</UserProvider>,
				);
			});

			const circle = screen.queryByRole("img");

			expect(circle).toBeFalsy();
		});
	});

	describe("displays userName sourced from userContext", () => {
		it("playerA", () => {
			act(() => {
				render(
					<UserProvider>
						<UserDispatcherInteractor dispatcherCommand={"setPlayerA"} />
					</UserProvider>,
				);
			});

			expect(screen.getByText("PlayerA")).toBeTruthy();
		});

		it("playerB", () => {
			act(() => {
				render(
					<UserProvider>
						<UserDispatcherInteractor dispatcherCommand={"setPlayerB"} />
					</UserProvider>,
				);
			});

			expect(screen.getByText("PlayerB")).toBeTruthy();
		});
	});

	describe("displays correct playerColor", () => {
		it("render correct color for playerA", () => {
			act(() => {
				render(
					<UserProvider>
						<UserDispatcherInteractor dispatcherCommand={"setPlayerA"} />
					</UserProvider>,
				);
			});
			const circle = screen.getByRole("img");

			expect(circle).toBeTruthy();
			expect(circle.querySelector("circle")?.getAttribute("fill")).toEqual(
				"#FF0000",
			);
		});

		it("render correct color for playerA", () => {
			act(() => {
				render(
					<UserProvider>
						<UserDispatcherInteractor dispatcherCommand={"setPlayerB"} />
					</UserProvider>,
				);
			});
			const circle = screen.getByRole("img");

			expect(circle).toBeTruthy();
			expect(circle.querySelector("circle")?.getAttribute("fill")).toEqual(
				"#0000FF",
			);
		});
	});
});

// Used to pass commands to the UserContext for testing
const UserDispatcherInteractor = ({
	dispatcherCommand,
}: {
	dispatcherCommand: string | null;
}) => {
	const dispatch = useDispatchUser();

	useEffect(() => {
		act(() => {
			if (dispatch && dispatcherCommand) {
				dispatch({
					type: dispatcherCommand,
					user: null,
				});
			}
		});
	}, [dispatch, dispatcherCommand]);

	return <Header />;
};
