import { test, expect } from "@playwright/test";
import { createRequire } from "node:module";
import { resolve, sep } from "node:path";

test.beforeEach(async ({ context, page }) => {
  // These are local UI tests: never contact Firebase, a backend, or analytics.
  await context.route("**/*", async (route) => {
    const url = new URL(route.request().url());
    if (url.hostname === "127.0.0.1" && url.port === "3117") {
      await route.continue();
    } else if (url.hostname === "firebase.googleapis.com" && url.pathname.endsWith("/webConfig")) {
      // Analytics initializes on page load even with the demo Firebase config.
      // Give it a deterministic local response instead of causing a fetch error.
      await route.fulfill({ json: { appId: "1:000000000000:web:0000000000000000000000", measurementId: "G-MIGRATIONTEST" } });
    } else if (url.hostname === "www.googletagmanager.com" && url.pathname === "/gtag/js") {
      await route.fulfill({ contentType: "application/javascript", body: "/* analytics disabled in UI tests */" });
    } else if (url.hostname === "firebaseinstallations.googleapis.com" && url.pathname === "/v1/projects/demo/installations") {
      await route.fulfill({ json: {
        fid: route.request().postDataJSON().fid,
        refreshToken: "synthetic-ui-test-refresh-token",
        authToken: { token: "synthetic-ui-test-token", expiresIn: "604800s" },
      } });
    } else {
      await route.abort();
    }
  });
  page.on("pageerror", (error) => { throw error; });
});

test("home page hydrates, expands FAQs, changes theme, and navigates", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("heading", { name: "Optimize your resume for every job application" })).toBeVisible();
  await page.getByRole("button", { name: "What is a resume scanner? Why is it better than manually optimizing?" }).click();
  await expect(page.getByText("A resume scanner analyzes your resume against job descriptions", { exact: false })).toBeVisible();
  const initialDark = await page.locator("html").evaluate((element) => element.classList.contains("dark"));
  await page.getByRole("button", { name: "Toggle theme" }).click();
  await expect.poll(() => page.locator("html").evaluate((element) => element.classList.contains("dark"))).toBe(!initialDark);
  await page.getByRole("link", { name: "Process", exact: true }).click();
  await expect(page).toHaveURL(/\/process$/);
  await expect(page.getByRole("heading", { name: "Technical Process" })).toBeVisible();
});

test("process tabs and nested accordion preserve their interactions", async ({ page }) => {
  await page.goto("/process");
  await page.getByRole("tab", { name: "Resume Parser", exact: true }).click();
  await page.getByRole("button", { name: "Document Processing Pipeline" }).click();
  await expect(page.getByText("Document conversion (PDF/DOCX to plain text)", { exact: true })).toBeVisible();
  await page.getByRole("tab", { name: "Overview", exact: true }).click();
  await expect(page.getByText("Tech Stack Overview", { exact: true })).toBeVisible();
});

test("auth UI validates locally and switches between sign-up and sign-in", async ({ page }) => {
  await page.goto("/auth");
  await expect(page.getByRole("heading", { name: "Create an account" })).toBeVisible();
  await page.getByRole("button", { name: "Sign Up with Email", exact: true }).click();
  await expect.poll(() => page.getByPlaceholder("name@example.com").evaluate((input) => input.validity.valueMissing)).toBe(true);
  await page.getByPlaceholder("name@example.com").fill("migration-test@example.invalid");
  await page.getByRole("button", { name: "Already have an account? Sign In" }).click();
  await expect(page.getByRole("heading", { name: "Sign in", exact: true })).toBeVisible();
  await expect(page.getByPlaceholder("name@example.com")).toHaveValue("migration-test@example.invalid");
  await page.getByRole("link", { name: "Back to home" }).click();
  await expect(page).toHaveURL("http://127.0.0.1:3117/");
});

test("dashboard opens and edits the existing new-scan dialog", async ({ page }) => {
  await page.goto("/dashboard");
  await expect(page.getByRole("heading", { name: "Welcome to SnapScreen" })).toBeVisible();
  await page.getByRole("button", { name: "New Scan", exact: true }).last().click();
  const dialog = page.getByRole("dialog");
  await expect(dialog.getByRole("heading", { name: "New scan", exact: true })).toBeVisible();
  await dialog.getByPlaceholder("Paste resume text...").fill("Synthetic resume for a UI test.");
  await dialog.getByPlaceholder("Copy and paste job description here").fill("Synthetic job description.");
  await expect(dialog.getByPlaceholder("Paste resume text...")).toHaveValue("Synthetic resume for a UI test.");
  await page.keyboard.press("Escape");
  await expect(dialog).not.toBeVisible();
});

test("standalone server serves every existing route and a real 404", async ({ request }) => {
  for (const path of ["/", "/auth", "/dashboard", "/dashboard/progress", "/process"]) {
    const response = await request.get(path);
    expect(response.status(), path).toBe(200);
    expect(response.headers()["content-type"], path).toContain("text/html");
  }
  expect((await request.get("/__migration_missing_route__")).status()).toBe(404);
});

test("standalone artifact contains the reviewed Next.js runtime", () => {
  const artifact = resolve(".next/standalone");
  const standaloneRequire = createRequire(resolve(artifact, "server.js"));
  const rootRequire = createRequire(import.meta.url);
  expect(standaloneRequire.resolve("next/package.json")).toContain(artifact + sep);
  expect(standaloneRequire("next/package.json").version).toBe(rootRequire("next/package.json").version);
});
