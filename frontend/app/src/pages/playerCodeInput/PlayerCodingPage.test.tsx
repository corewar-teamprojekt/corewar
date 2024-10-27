import { useToast } from "@/hooks/use-toast";
import { uploadPlayerCode } from "@/services/rest/RestService";
import { useUser } from "@/services/userContext/UserContextHelpers";
import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { act } from "react";
import { afterEach, beforeEach, describe, expect, it, Mock, vi } from "vitest";
import PlayerCodingPage from "./PlayerCodingPage";
import { createMemoryRouter, Navigate, RouterProvider } from "react-router-dom";

// Mock dependencies
vi.mock("@/services/userContext/UserContextHelpers");
vi.mock("@/services/rest/RestService");
vi.mock("@/hooks/use-toast");

const testRouterConfig = [
	{
		path: "/",
		element: <Navigate to="/player-coding" replace={true} />,
	},
	{
		path: "/player-coding",
		element: <PlayerCodingPage />,
	},
	{
		path: "/waiting-for-opponent",
		element: <div>Waiting for result</div>,
	},
];

describe("PlayerCodingPage", () => {
	const mockUser = { name: "testUser" };
	const mockToast = vi.fn();

	beforeEach(() => {
		(useUser as Mock).mockReturnValue(mockUser);
		(useToast as Mock).mockReturnValue({ toast: mockToast });
		global.window = Object.create(window);
	});

	afterEach(() => {
		vi.clearAllMocks();
	});

	it("opens confirm dialog when code is set", async () => {
		const router = createMemoryRouter(testRouterConfig);

		act(() => {
			render(<RouterProvider router={router} />);
		});

		const programInput = screen.getByRole("textbox");
		act(() => {
			fireEvent.change(programInput, { target: { value: "some code" } });
			fireEvent.click(screen.getByText("upload"));
		});
		await waitFor(() =>
			expect(
				screen.getByText((content) => content.startsWith("Are you sure")),
			).toBeVisible(),
		);
	});

	it("uploads code and shows success toast on confirm", async () => {
		(uploadPlayerCode as Mock).mockResolvedValueOnce({});
		const router = createMemoryRouter(testRouterConfig);
		act(() => {
			render(<RouterProvider router={router} />);
		});

		const programInput = screen.getByRole("textbox");
		act(() => {
			fireEvent.change(programInput, { target: { value: "some code" } });
			fireEvent.click(screen.getByText("upload"));
		});
		act(() => {
			fireEvent.click(screen.getByText("Confirm"));
		});
		await waitFor(() => {
			expect(uploadPlayerCode).toHaveBeenCalledWith("testUser", "some code");
			expect(mockToast).toHaveBeenCalledWith({
				title: "Success!",
				description: "your code has been uploaded",
			});
		});
	});

	it("shows error toast on upload failure", async () => {
		const errorMessage = "Upload failed";
		(uploadPlayerCode as Mock).mockRejectedValueOnce(new Error(errorMessage));
		const router = createMemoryRouter(testRouterConfig);
		act(() => {
			render(<RouterProvider router={router} />);
		});

		const programInput = screen.getByRole("textbox");
		act(() => {
			fireEvent.change(programInput, { target: { value: "some code" } });
			fireEvent.click(screen.getByText("upload"));
		});
		act(() => {
			fireEvent.click(screen.getByText("Confirm"));
		});

		await waitFor(() => {
			expect(uploadPlayerCode).toHaveBeenCalledWith("testUser", "some code");
			expect(mockToast).toHaveBeenCalledWith({
				title: "Error uploading code: ",
				description: errorMessage,
				variant: "destructive",
			});
		});
	});

	it("redirects to waiting-for-opponent page on successful upload", async () => {
		(uploadPlayerCode as Mock).mockResolvedValueOnce({});
		const router = createMemoryRouter(testRouterConfig);
		act(() => {
			render(<RouterProvider router={router} />);
		});

		const programInput = screen.getByRole("textbox");
		act(() => {
			fireEvent.change(programInput, { target: { value: "some code" } });
			const uploadButton = screen.getByText("upload");
			uploadButton.focus();
			uploadButton.click();
		});

		act(() => {
			const confirmButton = screen.getByText("Confirm");
			confirmButton.focus();
			confirmButton.click();
		});

		await waitFor(() => {
			expect(router.state.location.pathname).toEqual("/waiting-for-opponent");
		});
	});
});
