import { render, screen, fireEvent } from "@testing-library/react";
import ConfirmActionDialog from "./ConfirmActionDialog";
import "@testing-library/jest-dom";
import { afterEach, describe, expect, it, vi } from "vitest";

describe("ConfirmActionDialog", () => {
	const onConfirmMock = vi.fn();
	const setIsOpenMock = vi.fn();
	const onCancelMock = vi.fn();

	const defaultProps = {
		onConfirm: onConfirmMock,
		isOpen: true,
		setIsOpen: setIsOpenMock,
		onCancel: onCancelMock,
		title: "Test Title",
		description: "Test Description",
	};

	afterEach(() => {
		vi.clearAllMocks();
	});

	it("renders correctly with given props", () => {
		render(<ConfirmActionDialog {...defaultProps} />);
		expect(screen.getByText("Test Title")).toBeInTheDocument();
		expect(screen.getByText("Test Description")).toBeInTheDocument();
		expect(screen.getByText("Confirm")).toBeInTheDocument();
		expect(screen.getByText("Cancel")).toBeInTheDocument();
	});

	it("calls onConfirm and setIsOpen when Confirm button is clicked", () => {
		render(<ConfirmActionDialog {...defaultProps} />);
		fireEvent.click(screen.getByText("Confirm"));
		expect(onConfirmMock).toHaveBeenCalledTimes(1);
		expect(setIsOpenMock).toHaveBeenCalledWith(false);
	});

	it("calls onCancel and setIsOpen when Cancel button is clicked", () => {
		render(<ConfirmActionDialog {...defaultProps} />);
		fireEvent.click(screen.getByText("Cancel"));
		expect(onCancelMock).toHaveBeenCalledTimes(1);
		expect(setIsOpenMock).toHaveBeenCalledWith(false);
	});

	it("renders with default title and description if not provided", () => {
		render(
			<ConfirmActionDialog
				onConfirm={onConfirmMock}
				isOpen={true}
				setIsOpen={setIsOpenMock}
			/>,
		);
		expect(screen.getByText("Are you sure ?")).toBeInTheDocument();
		expect(
			screen.getByText("This action might be irreversible"),
		).toBeInTheDocument();
	});

	it("does not render when isOpen is false", () => {
		render(
			<ConfirmActionDialog
				onConfirm={onConfirmMock}
				isOpen={false}
				setIsOpen={setIsOpenMock}
			/>,
		);
		expect(screen.queryByText("Confirm")).not.toBeInTheDocument();
		expect(screen.queryByText("Cancel")).not.toBeInTheDocument();
	});
});
