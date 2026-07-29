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
  await page.locator('.card').filter({ hasText: 'iphone X' }).getByText('Add').click();
  await page.getByText('Checkout ( 1 )').click();
  await page.waitForTimeout(1500);

  console.log('=== body text ===');
  console.log(await page.locator('body').innerText());

  console.log('=== button texts ===');
  console.log(await page.locator('button').allTextContents());

  console.log('=== link texts ===');
  console.log(await page.locator('a').allTextContents());

  console.log('=== html snippet ===');
  console.log((await page.content()).slice(0, 4000));

  await browser.close();
})();
