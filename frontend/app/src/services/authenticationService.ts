import { User } from "../domain/user.ts";

class AuthenticationService {
	private currentUser: User | null = null;

	private playerA: User = new User("PlayerA", "0xFF0000");
	private playerB: User = new User("PlayerB", "0x0000FF");

	public switchToPlayerA(): void {
		this.setUser(this.playerA);
	}

	public switchToPlayerB(): void {
		this.setUser(this.playerB);
	}

	public setUser(user: User): void {
		this.currentUser = user;
	}

	public getUser(): User | null {
		return this.currentUser;
	}
}

export default AuthenticationService;
