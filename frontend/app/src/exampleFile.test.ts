import {expect, it} from "@jest/globals";
import exampleFunction from "./exampleFile.ts";

it('adds a to b', () => {
    expect(2).toBe(exampleFunction(1, 1));
})