import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests/e2e",
  workers: 1,
  retries: 0,
  use: {
    baseURL: "http://127.0.0.1:3117",
    browserName: "chromium",
    ...(process.env.PLAYWRIGHT_CHANNEL ? { channel: process.env.PLAYWRIGHT_CHANNEL } : {}),
    trace: "retain-on-failure",
  },
  webServer: {
    command: "node .next/standalone/server.js",
    url: "http://127.0.0.1:3117",
    env: { HOSTNAME: "127.0.0.1", PORT: "3117", NEXT_TELEMETRY_DISABLED: "1" },
    reuseExistingServer: false,
    timeout: 30_000,
  },
});
