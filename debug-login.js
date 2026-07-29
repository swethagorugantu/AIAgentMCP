const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  await page.goto('https://rahulshettyacademy.com/loginpagePractise/');
  await page.locator('input[name="username"]').fill('rahulshettyacademy');
  await page.locator('input[name="password"]').fill('Learning@830$3mK2');
  await page.locator('input[type="checkbox"]').check();
  await page.locator('input[type="submit"]').click();
  await page.waitForSelector('.card', { timeout: 20000 });

  const cards = await page.locator('.card').allTextContents();
  console.log('card count', cards.length);
  console.log(JSON.stringify(cards.slice(0, 8), null, 2));

  await browser.close();
})();
