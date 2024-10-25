import {
	useDispatchUser,
	useUser,
} from "@/services/userContext/UserContextHelpers";
import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import { describe, expect, it, Mock, vi } from "vitest";
import { RequireLogout } from "./RequireLogout";

vi.mock("@/services/userContext/UserContextHelpers");

describe("RequireLogout", () => {
	const requiredText = "No users here pwease :3";

	it("renders children when user is not logged in", () => {
		(useUser as Mock).mockReturnValue(null);
		(useDispatchUser as Mock).mockReturnValue(vi.fn());

		render(
			<RequireLogout blocked={false}>
				<p>{requiredText}</p>
			</RequireLogout>,
		);
		screen.debug();
		expect(screen.getByText(requiredText)).toBeInTheDocument();
	});

	it("dispatches logout action when user is logged in", () => {
		const mockDispatcher = vi.fn();
		(useUser as Mock).mockReturnValue({ name: "testUser" });
		(useDispatchUser as Mock).mockReturnValue(mockDispatcher);

		render(
			<RequireLogout blocked={false}>
				<p>{requiredText}</p>
			</RequireLogout>,
		);

		expect(mockDispatcher).toHaveBeenCalledWith({
			type: "logout",
			user: null,
		});
	});
});
