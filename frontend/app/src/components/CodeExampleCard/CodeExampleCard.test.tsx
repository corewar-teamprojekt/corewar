import { render, screen, fireEvent } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import { CodeExampleCard } from "./CodeExampleCard";
import { act } from "react";
import "@testing-library/jest-dom";

describe("CodeExampleCard", () => {
	const mockSetCode = vi.fn();

	const defaultProps = {
		title: "Example Title",
		shortDescription: "This is a short description.",
		setCode: mockSetCode,
	};

	it("renders the card with title and description", () => {
		act(() => {
			render(<CodeExampleCard {...defaultProps} />);
		});
		expect(screen.getByText(defaultProps.title)).toBeInTheDocument();
		expect(screen.getByText(defaultProps.shortDescription)).toBeInTheDocument();
	});

	it("opens the confirm dialog when 'Paste code' button is clicked", () => {
		act(() => {
			render(<CodeExampleCard {...defaultProps} />);
		});
		const pasteButton = screen.getByText("Paste code");
		fireEvent.click(pasteButton);
		expect(
			screen.getByText("Paste example code into editor"),
		).toBeInTheDocument();
		expect(
			screen.getByText(
				"Are you sure you want to paste the example code into the editor? This will overwrite your current code.",
			),
		).toBeInTheDocument();
	});

	it("calls setCode when confirm button in dialog is clicked", () => {
		act(() => {
			render(<CodeExampleCard {...defaultProps} />);
		});
		const pasteButton = screen.getByText("Paste code");
		fireEvent.click(pasteButton);
		const confirmButton = screen.getByText("Confirm");
		fireEvent.click(confirmButton);
		expect(mockSetCode).toHaveBeenCalled();
	});

	it("closes the confirm dialog when cancel button in dialog is clicked", () => {
		act(() => {
			render(<CodeExampleCard {...defaultProps} />);
		});
		const pasteButton = screen.getByText("Paste code");
		fireEvent.click(pasteButton);
		const cancelButton = screen.getByText("Cancel");
		fireEvent.click(cancelButton);
		expect(
			screen.queryByText("Paste example code into editor"),
		).not.toBeInTheDocument();
	});
});
