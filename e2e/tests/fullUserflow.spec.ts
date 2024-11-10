import {expect, test} from "@playwright/test";

test("complete userflow", async ({ browser }) => {
    const playerAContext = await browser.newContext();
    const playerAPage = await playerAContext.newPage();

    const playerBContext = await browser.newContext();
    const playerBPage = await playerBContext.newPage();

    await playerAPage.goto("/");
    await playerAPage.getByText("Play").click();

    await playerAPage.locator('div').filter({ hasText: /^PLAYER APLAY$/ }).getByRole('button').click();

    await playerAPage.getByRole("textbox").focus()
    await playerAPage.locator('.view-lines').click();
    await playerAPage.keyboard.type('ADD 1, 1');
    await playerAPage.getByRole('button', { name: 'upload' }).click();
    await playerAPage.getByRole('button', { name: 'Confirm' }).click();

    await expect(playerAPage.getByText("Waiting for opponent...")).toBeVisible();

    await playerBPage.goto("/");
    await playerBPage.getByText("Play").click();

    await playerBPage.locator('div').filter({ hasText: /^PLAYER BPLAY$/ }).getByRole('button').click();

    await playerBPage.getByRole("textbox").focus()
    await playerBPage.locator('.view-lines').click();
    await playerBPage.keyboard.type('ADD 1, 1');
    await playerBPage.getByRole('button', { name: 'upload' }).click();
    await playerBPage.getByRole('button', { name: 'Confirm' }).click();

    await expect(playerBPage.getByText("Waiting for opponent...")).toBeVisible();

    await expect(playerAPage.getByText("Waiting for game result...")).toBeVisible();
    await expect(playerBPage.getByText("Waiting for game result...")).toBeVisible();

    await expect(playerAPage.getByText("Result: Player A vs Player B")).toBeVisible();
    await expect(playerBPage.getByText("Result: Player A vs Player B")).toBeVisible();

    const playAgainButton = playerAPage.getByRole('button', { name: 'Play again' });
    await expect(playAgainButton).toBeVisible();
    await playAgainButton.click();

    await expect(playerAPage).toHaveURL(/.*player-selection/);
});