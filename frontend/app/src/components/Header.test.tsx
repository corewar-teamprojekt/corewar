import { beforeEach, describe, expect, it } from "vitest";
import { act, useEffect } from "react";
import { useDispatchUser, UserProvider } from "@/services/UserContext.tsx";
import Header from "@/components/Header.tsx";
import { cleanup, render, screen } from "@testing-library/react";

beforeEach(() => {
	cleanup();
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

// Used to pass commands to the UserContext for testing
const UserDispatcherInteractor = ({
	dispatcherCommand,
}: {
	dispatcherCommand: string;
}) => {
	const dispatch = useDispatchUser();

	useEffect(() => {
		act(() => {
			if (dispatch) {
				dispatch({
					type: dispatcherCommand,
					user: null,
				});
			}
		});
	}, [dispatch, dispatcherCommand]);

	return <Header />;
};
