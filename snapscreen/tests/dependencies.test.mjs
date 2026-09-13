import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { createRequire } from "node:module";
import { basename, join } from "node:path";
import { tmpdir } from "node:os";
import test from "node:test";

const require = createRequire(import.meta.url);
const lock = JSON.parse(readFileSync(new URL("../package-lock.json", import.meta.url), "utf8"));

// Require a stable release at or above the advisory's maintained fix lines.
function isPatched(version) {
  if (!/^\d+\.\d+\.\d+$/.test(version)) return false;
  const [major, minor, patch] = version.split(".").map(Number);
  if (major > 16) return true;
  if (major === 16) return minor > 3 || (minor === 3 && patch >= 3);
  return major === 15 && (minor > 5 || (minor === 5 && patch >= 24));
}

test("the advisory guard rejects affected and prerelease versions", () => {
  for (const version of ["14.1.3", "15.5.23", "16.0.0", "16.3.2", "16.3.3-canary.1"]) {
    assert.equal(isPatched(version), false, version);
  }
  for (const version of ["15.5.24", "16.3.3", "16.3.5"]) {
    assert.equal(isPatched(version), true, version);
  }
});

test("every locked Next.js copy is outside GHSA-p293-qw3h-jr36 affected versions", () => {
  const copies = Object.entries(lock.packages).filter(([path]) => path.endsWith("node_modules/next"));
  assert.ok(copies.length > 0, "Next.js must be present");
  for (const [path, dependency] of copies) {
    assert.ok(isPatched(dependency.version), `${path}: ${dependency.version}`);
  }
});

test("the installed Next.js version matches the reviewed lockfile", () => {
  const installed = require("next/package.json").version;
  assert.equal(installed, lock.packages["node_modules/next"].version);
  assert.ok(isPatched(installed));
});

// Exercise the actual two upstream fix boundaries without writing cache files.
// https://github.com/vercel/next.js/commit/968b9fcb26bdeb8e0a861a9df05361474666d51b
test("route segments escape raw and encoded Windows separators", () => {
  const escapePathDelimiters = require("next/dist/shared/lib/router/utils/escape-path-delimiters.js").default;
  assert.equal(escapePathDelimiters("..\\..\\private"), "..%5C..%5Cprivate");
  assert.equal(escapePathDelimiters("..%5C..%5Cprivate", true), "..%255C..%255Cprivate");
  assert.equal(escapePathDelimiters("ordinary-page_1.json"), "ordinary-page_1.json");
});

for (const kind of ["FETCH", "PAGES", "IMAGE", "APP_PAGE", "APP_ROUTE"]) {
  test(`${kind} cache paths reject traversal and preserve ordinary nested keys`, () => {
    const FileSystemCache = require("next/dist/server/lib/incremental-cache/file-system-cache.js").default;
    const cache = Object.create(FileSystemCache.prototype);
    cache.serverDistDir = join(tmpdir(), "next-migration-check", ".next", "server");
    const root = kind === "FETCH"
      ? join(cache.serverDistDir, "..", "cache", "fetch-cache")
      : join(cache.serverDistDir, kind === "PAGES" ? "pages" : "app");
    assert.equal(cache.getFilePath("ordinary/page.json", kind), join(root, "ordinary/page.json"));
    for (const key of [
      join("..", "outside.json"),
      join("..", `${basename(root)}-sibling`, "outside.json"),
      join("ordinary", "..", "..", "outside.json"),
    ]) {
      assert.throws(() => cache.getFilePath(key, kind), /Invalid file path/, key);
    }
  });
}
