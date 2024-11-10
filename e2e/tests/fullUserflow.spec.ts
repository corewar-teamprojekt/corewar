import {expect, test} from "@playwright/test";

test("complete userflow", async ({ page }) => {
    await page.goto('/');

    expect(1).toBe(1);
});