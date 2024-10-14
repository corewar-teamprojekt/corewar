import { beforeEach, describe, expect, it } from "@jest/globals";
import AuthenticationService from "./authenticationService.ts";
import { User } from "../domain/user.ts";

describe("AuthenticationService", () => {
	let authenticationService: AuthenticationService;

	beforeEach(() => {
		authenticationService = new AuthenticationService();
	});

	describe("mvp", () => {
		it("is able to switch to playerA while currentUser is null", () => {
			authenticationService.switchToPlayerA();
			const receivedUser = authenticationService.getUser();
			expect(receivedUser).toBeDefined();
			expect(receivedUser?.name).toEqual("PlayerA");
			expect(receivedUser?.colorHex).toEqual("0xFF0000");
		});

		it("is able to switch to playerB while currentUser is playerA", () => {
			authenticationService.setUser(new User("PlayerA", "0xFF0000"));
			authenticationService.switchToPlayerB();
			const receivedUser = authenticationService.getUser();
			expect(receivedUser).toBeDefined();
			expect(receivedUser?.name).toEqual("PlayerB");
			expect(receivedUser?.colorHex).toEqual("0x0000FF");
		});
	});

	describe("raw functionality", () => {
		it("inits with null as user", () => {
			expect(authenticationService.getUser()).toBeNull();
		});

		it("setting and getting", () => {
			const aUser = new User("PlayerA", "0xFF0000");
			authenticationService.setUser(aUser);

			const receivedUser = authenticationService.getUser();
			expect(receivedUser).toBeDefined();
			expect(receivedUser?.name).toEqual("PlayerA");
			expect(receivedUser?.colorHex).toEqual("0xFF0000");
		});
	});
});
