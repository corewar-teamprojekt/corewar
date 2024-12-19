import { expect, test } from "@playwright/test";

test("complete userflow", async ({ browser }) => {
    const playerAContext = await browser.newContext();
    const playerAPage = await playerAContext.newPage();

    const playerBContext = await browser.newContext();
    const playerBPage = await playerBContext.newPage();

    // Landing page
    await playerAPage.goto("/");
    await playerAPage.locator('button:has-text("Play")').click();

    // Lobby selection page
    await playerAPage.getByRole("button", { name: "create lobby" }).click();

    // Player selection page
    await playerAPage.locator('button:has([alt="Player A Icon"])').click();

    // Player code input page
    await playerAPage.getByRole("textbox").focus();
    await playerAPage.locator(".view-lines").click();
    await playerAPage.keyboard.type("ADD 1, 1");
    await playerAPage.getByRole("button", { name: "upload" }).click();
    await playerAPage.getByRole("button", { name: "Confirm" }).click();

    // Waiting for opponent page
    await expect(playerAPage.getByText("Waiting for opponent..."))
        .toBeVisible();

    // Landing page
    await playerBPage.goto("/");
    await playerBPage.locator('button:has-text("Play")').click();

    // Lobby selection page
    await playerBPage.getByRole("button", { name: "join" }).click();

    // Player selection page
    await playerBPage.locator('button:has([alt="Player B Icon"])').click();

    // Player code input page
    await playerBPage.getByRole("textbox").focus();
    await playerBPage.locator(".view-lines").click();
    await playerBPage.keyboard.type("ADD 1, 1");
    await playerBPage.getByRole("button", { name: "upload" }).click();
    await playerBPage.getByRole("button", { name: "Confirm" }).click();

    // Waiting for opponent page
    await expect(playerBPage.getByText("Waiting for opponent...")).toBeVisible({
        timeout: 60000,
    });

    await expect(playerAPage.getByText("Waiting for game result..."))
        .toBeVisible({ timeout: 60000 });
    await expect(playerBPage.getByText("Waiting for game result..."))
        .toBeVisible({ timeout: 60000 });

    const skipVisuButtonA = playerAPage.getByRole("button", {
        name: "skip visualization>>",
    });
    await expect(skipVisuButtonA).toBeVisible();
    await skipVisuButtonA.dispatchEvent('click')

    const skipVisuButtonB = playerBPage.getByRole("button", {
        name: "skip visualization>>",
    });
    await expect(skipVisuButtonB).toBeVisible();
    await skipVisuButtonB.dispatchEvent('click')

    await expect(playerAPage.getByText("It's a draw!")).toBeVisible({
        timeout: 60000,
    });
    await expect(playerBPage.getByText("It's a draw!")).toBeVisible({
        timeout: 60000,
    });

    const playAgainButton = playerAPage.getByRole("button", {
        name: "Play again",
    });
    await expect(playAgainButton).toBeVisible();
    await playAgainButton.click();

    await expect(playerAPage).toHaveURL(/.*lobby-selection/);

});
