const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  page.on('request', req => {
    if (req.url().includes('rahulshettyacademy.com')) {
      console.log('REQUEST', req.method(), req.url());
      if (req.postData()) console.log(req.postData());
    }
  });
  page.on('response', async res => {
    if (res.url().includes('rahulshettyacademy.com')) {
      try {
        const body = await res.text();
        if (body) console.log('RESPONSE', res.status(), res.url(), body.slice(0, 1000));
      } catch (e) {}
    }
  });

  await page.goto('https://rahulshettyacademy.com/client');
  await page.getByText(/Register here/i).click();
  await page.waitForTimeout(1500);
  await page.locator('#firstName').fill('John');
  await page.locator('#lastName').fill('Doe');
  const email = 'testuser' + Date.now() + '@example.com';
  await page.locator('#userEmail').fill(email);
  await page.locator('#userMobile').fill('9999999999');
  await page.locator('select').selectOption('Engineer');
  await page.locator('input[type="radio"]').nth(0).check();
  await page.locator('#userPassword').fill('TestPass123!');
  await page.locator('#confirmPassword').fill('TestPass123!');
  await page.locator('input[type="checkbox"]').check();
  await page.locator('input[type="submit"]').click();
  await page.waitForTimeout(5000);
  await browser.close();
})();
