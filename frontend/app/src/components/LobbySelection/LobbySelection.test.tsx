import { Lobby } from "@/domain/Lobby";
import { mockLobbies } from "@/TestFactories";
import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
} from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import LobbySelection from "./LobbySelection";

const mockJoinLobby = vi.fn();

describe("LobbySelection", () => {
	it("renders the correct lobbies", () => {
		render(
			<LobbySelection lobbies={mockLobbies()} joinLobby={mockJoinLobby} />,
		);
		for (const lobby of mockLobbies()) {
			const row = screen.getByText("LobbyID: " + lobby.id).closest("tr");
			expect(row).toBeTruthy();
		}
	});

	it("filters lobbies based on search input", () => {
		render(
			<LobbySelection lobbies={mockLobbies()} joinLobby={mockJoinLobby} />,
		);
		act(() => {
			const input = screen.getByPlaceholderText("search for lobbyID");
			fireEvent.change(input, { target: { value: "2" } });
		});
		const rows = screen.getAllByRole("row");
		expect(rows).toHaveLength(1);
		expect(rows[0].querySelector("td")?.textContent).toContain("LobbyID: 2");
	});

	it("disables the join button for full lobbies", () => {
		render(
			<LobbySelection lobbies={mockLobbies()} joinLobby={mockJoinLobby} />,
		);
		for (const fullLobby of getFullLobbies()) {
			waitFor(() => {
				const row = screen.getByText("LobbyID: " + fullLobby.id).closest("tr");
				const button = row?.querySelector("button");
				expect(button).toBeDisabled();
			});
		}
	});

	it("enables the join button for available lobbies", () => {
		render(
			<LobbySelection lobbies={mockLobbies()} joinLobby={mockJoinLobby} />,
		);
		for (const fullLobby of getFreeLobbies()) {
			waitFor(() => {
				const row = screen.getByText("LobbyID: " + fullLobby.id).closest("tr");
				const button = row?.querySelector("button");
				expect(button).toBeEnabled();
			});
		}
	});

	it("calls joinLobby function when join button is clicked", () => {
		render(
			<LobbySelection lobbies={mockLobbies()} joinLobby={mockJoinLobby} />,
		);
		const freeLobby = getFreeLobbies()[0];
		act(() => {
			const row = screen.getByText("LobbyID: " + freeLobby.id).closest("tr");
			const button = row?.querySelector("button");
			button?.click();
		});
		waitFor(() => {
			expect(mockJoinLobby).toHaveBeenCalledWith(freeLobby);
		});
	});
});

function getFullLobbies(): Lobby[] {
	const lobbies = mockLobbies();
	const fullLobbies = [];
	for (const lobby of lobbies) {
		if (lobby.isLobbyFull()) {
			fullLobbies.push(lobby);
		}
	}
	return fullLobbies;
}

function getFreeLobbies(): Lobby[] {
	const lobbies = mockLobbies();
	const fullLobbies = [];
	for (const lobby of lobbies) {
		if (lobby.isLobbyFull() === false) {
			fullLobbies.push(lobby);
		}
	}
	return fullLobbies;
}
