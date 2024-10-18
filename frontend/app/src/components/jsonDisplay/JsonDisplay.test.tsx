import { beforeEach, describe, expect, it } from "vitest";
import { cleanup, render, screen } from "@testing-library/react";
import { act } from "react";
import JsonDisplay from "@/components/jsonDisplay/JsonDisplay.tsx";

beforeEach(() => {
	cleanup();
});

describe("json display", () => {
	it("displays passed json prop as text", () => {
		const jsonObject = {
			this: "is",
			a: {
				sample: "object",
			},
		};

		act(() => {
			render(<JsonDisplay json={jsonObject} />);
		});

		expect(
			screen.getByText(JSON.stringify(jsonObject, null, 2), {
				// Turn off normalizer to find prettified json
				normalizer: (str: string) => {
					return str;
				},
			}),
		).toBeTruthy();
	});
});
