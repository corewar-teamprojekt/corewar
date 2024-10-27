import { User } from "@/domain/User.ts";
import { useToast } from "@/hooks/use-toast";
import { useUser } from "@/services/userContext/UserContextHelpers";
import "@testing-library/jest-dom";
import { render, screen, waitFor } from "@testing-library/react";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, Mock, vi } from "vitest";
import { RequireUser } from "./RequireUser";

// Mock the hooks
vi.mock("@/services/userContext/UserContextHelpers", () => ({
	useUser: vi.fn(),
}));

vi.mock("@/hooks/use-toast", () => ({
	useToast: vi.fn(),
}));

const protectedText = "I needs a user pwease >w<";
const testRouterConfig = [
	{
		path: "/",
		element: <Navigate to="/requires-login" replace={true} />,
	},
	{
		path: "/requires-login",
		element: (
			<RequireUser>
				<p>{protectedText}</p>
			</RequireUser>
		),
	},
	{
		path: "/player-selection",
		element: <div />,
	},
];

describe("RequireUser", () => {
	const mockToast = vi.fn();

	beforeEach(() => {
		(useToast as Mock).mockReturnValue({ toast: mockToast });
	});

	afterEach(() => {
		vi.clearAllMocks();
	});

	it("renders children when user is present", async () => {
		(useUser as Mock).mockReturnValue(new User("playerA", "#ffeeff"));

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);

		expect(screen.getByText(protectedText)).toBeInTheDocument();
		expect(mockToast).not.toHaveBeenCalled();
		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/requires-login");
		});
	});

	it("redirects to /player-selection and shows toast when user is not present", async () => {
		(useUser as Mock).mockReturnValue(null);

		const router = createMemoryRouter(testRouterConfig);
		render(<RouterProvider router={router} />);

		expect(mockToast).toHaveBeenCalledWith({
			title: "Oopsie👉👈",
			description: "You have to be logged in to be here 😢",
			variant: "destructive",
		});
		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/player-selection");
		});
	});
});
