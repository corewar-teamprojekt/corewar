import { useToast } from "@/hooks/use-toast";
import { uploadPlayerCode } from "@/services/rest/RestService";
import { useUser } from "@/services/userContext/UserContextHelpers";
import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { act } from "react";
import { afterEach, beforeEach, describe, expect, it, Mock, vi } from "vitest";
import PlayerCodingPage from "./PlayerCodingPage";

// Mock dependencies
vi.mock("@/services/userContext/UserContextHelpers");
vi.mock("@/services/rest/RestService");
vi.mock("@/hooks/use-toast");

describe("PlayerCodingPage", () => {
	const mockUser = { name: "testUser" };
	const mockToast = vi.fn();

	beforeEach(() => {
		(useUser as Mock).mockReturnValue(mockUser);
		(useToast as Mock).mockReturnValue({ toast: mockToast });
		global.window = Object.create(window);
		global.window.location = {
			ancestorOrigins: {
				length: 0,
				contains: () => false,
				item: () => null,
			} as unknown as DOMStringList,
			hash: "",
			host: "dummy.com",
			port: "80",
			protocol: "http:",
			hostname: "dummy.com",
			href: "http://dummy.com?page=1&name=testing",
			origin: "http://dummy.com",
			pathname: "",
			search: "",
			assign: () => {},
			reload: () => {},
			replace: () => {},
		};
	});

	afterEach(() => {
		vi.clearAllMocks();
	});

	it("opens confirm dialog when code is set", async () => {
		act(() => {
			render(<PlayerCodingPage />);
		});
		const programInput = screen.getByRole("textbox");
		act(() => {
			fireEvent.change(programInput, { target: { value: "some code" } });
			fireEvent.click(screen.getByText("upload"));
		});
		await waitFor(() =>
			expect(
				screen.getByText((content) => content.startsWith("Are you sure")),
			).toBeInTheDocument(),
		);
	});

	it("uploads code and shows success toast on confirm", async () => {
		(uploadPlayerCode as Mock).mockResolvedValueOnce({});
		render(<PlayerCodingPage />);

		const programInput = screen.getByRole("textbox");
		fireEvent.change(programInput, { target: { value: "some code" } });
		fireEvent.click(screen.getByText("upload"));
		fireEvent.click(screen.getByText("Confirm"));

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
		render(<PlayerCodingPage />);

		const programInput = screen.getByRole("textbox");
		fireEvent.change(programInput, { target: { value: "some code" } });
		fireEvent.click(screen.getByText("upload"));
		fireEvent.click(screen.getByText("Confirm"));

		await waitFor(() => {
			expect(uploadPlayerCode).toHaveBeenCalledWith("testUser", "some code");
			expect(mockToast).toHaveBeenCalledWith({
				title: "Error uploading code: ",
				description: errorMessage,
				variant: "destructive",
			});
		});
	});

	it("redirects to waiting-for-result page on successful upload", async () => {
		(uploadPlayerCode as Mock).mockResolvedValueOnce({});
		render(<PlayerCodingPage />);

		const programInput = screen.getByRole("textbox");
		act(() => {
			fireEvent.change(programInput, { target: { value: "some code" } });
			fireEvent.click(screen.getByText("upload"));
		});
		act(() => fireEvent.click(screen.getByText("Confirm")));
		await new Promise((resolve) => setTimeout(resolve, 1000));
		await waitFor(() => {
			expect(window.location.href).toContain("/waiting-for-result");
		});
	});
});
