// later when more api endpoints are added, this can be split into multiple files

import { API_URL } from "@/lib/config";

export function uploadPlayerCode(
	playerName: string,
	code: string,
): Promise<Response> {
	return fetch(API_URL + "/code/" + playerName, {
		method: "POST",
		body: code,
	});
}
