// later when more api endpoints are added, this can be split into multiple files

import { BACKEND_BASE_URL } from "@/domain/consts";

export function uploadPlayerCode(
	playerName: string,
	code: string,
): Promise<Response> {
	return fetch(BACKEND_BASE_URL + "/v0/code/" + playerName, {
		method: "POST",
		body: code,
	});
}
