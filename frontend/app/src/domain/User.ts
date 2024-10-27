import { ValidUsernames } from "./validUsernames.ts";

export class User {
	public name: string;
	public colorHex: string;

	constructor(name: ValidUsernames, colorHex: string) {
		this.name = name;
		this.colorHex = colorHex;
	}
}
