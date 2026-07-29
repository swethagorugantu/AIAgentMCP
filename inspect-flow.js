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

  const iphoneCard = page.locator('.card').filter({ hasText: 'iphone X' });
  await iphoneCard.locator('text=Add').click();
  await page.waitForTimeout(1000);

  const checkoutLink = page.getByText('Checkout ( 1 )');
  await checkoutLink.click();
  await page.waitForTimeout(1500);
  console.log('CART BODY:\n' + await page.locator('body').innerText());

  await page.locator('button').filter({ hasText: 'Checkout' }).click();
  await page.waitForTimeout(1500);
  console.log('CHECKOUT BODY:\n' + await page.locator('body').innerText());

  await page.locator('input[name="name"]').fill('John');
  await page.locator('input[name="country"]').fill('India');
  await page.locator('text=Purchase').click();
  await page.waitForTimeout(3000);
  console.log('PURCHASE BODY:\n' + await page.locator('body').innerText());

  await browser.close();
})();
