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

  const buttons = await page.locator('button').allTextContents();
  const links = await page.locator('a').allTextContents();
  const inputs = await page.locator('input, select').evaluateAll((els) =>
    els.map((e) => ({
      tag: e.tagName,
      name: e.getAttribute('name'),
      type: e.getAttribute('type'),
      id: e.getAttribute('id'),
      className: e.getAttribute('class'),
      placeholder: e.getAttribute('placeholder'),
      value: e.getAttribute('value'),
      text: e.textContent,
      visible: !!(e.offsetWidth || e.offsetHeight || e.getClientRects().length)
    }))
  );

  console.log('BUTTONS', JSON.stringify(buttons, null, 2));
  console.log('LINKS', JSON.stringify(links, null, 2));
  console.log('INPUTS', JSON.stringify(inputs, null, 2));

  await browser.close();
})();
