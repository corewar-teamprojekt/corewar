// later when more api endpoints are added, this can be split into multiple files

export function uploadPlayerCode(
	playerName: string,
	code: string,
): Promise<Response> {
	return fetch(
		`${import.meta.env.VITE_REACT_APP_BACKEND_URL}/v0/code/${playerName}`,
		{
			method: "POST",
			body: code,
		},
	);
}
