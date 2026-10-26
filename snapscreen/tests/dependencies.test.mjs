import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { createRequire } from "node:module";
import { basename, join } from "node:path";
import { tmpdir } from "node:os";
import test from "node:test";
import { SourceMapConsumer, SourceMapGenerator } from "source-map-js";
import { z } from "zod";

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

// The October cache, metadata, image and development-server advisories have
// newer maintained fix lines than the original Windows traversal advisory.
function isPatchedForOctober(version) {
  if (!/^\d+\.\d+\.\d+$/.test(version)) return false;
  const [major, minor, patch] = version.split(".").map(Number);
  if (major > 16) return true;
  if (major === 16) return minor > 3 || (minor === 3 && patch >= 8);
  return major === 15 && (minor > 5 || (minor === 5 && patch >= 27));
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

test("the October guard rejects affected and prerelease Next versions", () => {
  for (const version of ["15.5.24", "15.5.26", "16.0.0", "16.3.3", "16.3.6", "16.3.7", "16.3.8-canary.1"]) {
    assert.equal(isPatchedForOctober(version), false, version);
  }
  for (const version of ["15.5.27", "16.3.8", "16.4.0"]) {
    assert.equal(isPatchedForOctober(version), true, version);
  }
});

test("every locked Next.js copy is outside the October advisory ranges", () => {
  const copies = Object.entries(lock.packages).filter(([path]) => path.endsWith("node_modules/next"));
  assert.ok(copies.length > 0, "Next.js must be present");
  for (const [path, dependency] of copies) {
    assert.ok(isPatchedForOctober(dependency.version), `${path}: ${dependency.version}`);
  }
});

// The advisories published to the database on October 22 require newer fixes.
function isPatchedForLateOctober(version) {
  if (!/^\d+\.\d+\.\d+$/.test(version)) return false;
  const [major, minor, patch] = version.split(".").map(Number);
  if (major > 16) return true;
  if (major === 16) return minor > 4 || (minor === 4 && patch >= 1);
  return major === 15 && (minor > 5 || (minor === 5 && patch >= 28));
}

test("late-October advisory floor rejects affected and prerelease Next versions", () => {
  for (const version of ["15.5.27", "16.3.8", "16.4.0", "16.4.1-canary.1"]) {
    assert.equal(isPatchedForLateOctober(version), false, version);
  }
  for (const version of ["15.5.28", "16.4.1", "16.4.3"]) {
    assert.equal(isPatchedForLateOctober(version), true, version);
  }
});

test("all locked Next copies meet the late-October advisory floor", () => {
  const copies = Object.entries(lock.packages).filter(([path]) => path.endsWith("node_modules/next"));
  assert.ok(copies.length > 0);
  for (const [path, dependency] of copies) {
    assert.ok(isPatchedForLateOctober(dependency.version), `${path}: ${dependency.version}`);
  }
  assert.equal(require("next/package.json").version, lock.packages["node_modules/next"].version);
});

// Zod is dev-only ESLint tooling, not a new application input parser.
for (const jitless of [false, true]) {
  for (const [name, schema, oversized, valid] of [
    ["array", () => z.array(z.number()).max(2), () => Array(1000).fill("invalid"), () => [1, 2]],
    ["set", () => z.set(z.number()).max(2), () => new Set(Array.from({ length: 1000 }, (_, i) => `bad-${i}`)), () => new Set([1, 2])],
    ["map", () => z.map(z.string(), z.number()).max(2), () => new Map(Array.from({ length: 1000 }, (_, i) => [i, "invalid"])), () => new Map([["one", 1], ["two", 2]])],
  ]) {
    test(`Zod ${name} bounds reject excessive element work and retain valid values (jitless=${jitless})`, () => {
      const parser = z.object({ items: schema() });
      const result = parser.safeParse({ items: oversized() }, { jitless });
      assert.equal(result.success, false);
      assert.equal(result.error.issues.length, 1);
      assert.equal(result.error.issues[0].code, "too_big");
      assert.deepEqual(result.error.issues[0].path, ["items"]);
      const value = { items: valid() };
      assert.deepEqual(parser.parse(value, { jitless }), value);
    });
  }
}

test("patched Sharp native processing preserves pixels and rejects malformed input", async () => {
  const sharp = require("sharp");
  const version = require("sharp/package.json").version;
  assert.equal(version, lock.packages["node_modules/sharp"].version);
  assert.match(version, /^\d+\.\d+\.\d+$/);
  const [major, minor, patch] = version.split(".").map(Number);
  assert.ok(major > 0 || minor > 35 || (minor === 35 && patch >= 5));
  const manifest = JSON.parse(readFileSync(new URL("../package.json", import.meta.url), "utf8"));
  assert.equal(manifest.allowScripts[`sharp@${version}`], true);
  const input = await sharp({
    create: { width: 2, height: 2, channels: 3, background: { r: 255, g: 0, b: 0 } },
  }).png().toBuffer();
  const result = await sharp(input).resize(1, 1).raw().toBuffer({ resolveWithObject: true });
  assert.equal(result.info.width, 1);
  assert.equal(result.info.height, 1);
  assert.deepEqual(result.data, Buffer.from([255, 0, 0]));
  await assert.rejects(sharp(Buffer.from("malformed image fixture")).metadata(), /unsupported image format/i);
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

const basicMap = () => ({
  version: 3,
  sources: ["input.js"],
  sourcesContent: ["const value = 1;"],
  names: [],
  mappings: "AAAA",
});

function indexedMap(line, column = 0, map = basicMap()) {
  return { version: 3, sections: [{ offset: { line, column }, map }] };
}

test("indexed source maps reject non-integer and non-numeric section offsets", () => {
  // GHSA-68fv-2mgg-jv7q: unsafe offsets amplify work when maps are flattened.
  const invalid = [-1, 0.5, NaN, Infinity, -Infinity, Number.MAX_SAFE_INTEGER + 1,
    "1", null, undefined, {}];
  for (const value of invalid) {
    for (const field of ["line", "column"]) {
      const map = indexedMap(0);
      map.sections[0].offset[field] = value;
      assert.throws(() => new SourceMapConsumer(map), /non-negative integers/,
        `${field}=${String(value)}`);
    }
  }
});

test("indexed source maps reject excessive direct and cumulative line offsets", () => {
  assert.throws(() => new SourceMapConsumer(indexedMap(10_000_001)), /must not exceed/);
  const inner = indexedMap(5_000_000);
  const atLimit = indexedMap(5_000_000, 0, inner);
  const valid = new SourceMapConsumer(atLimit);
  const generatedLines = [];
  valid.eachMapping((mapping) => generatedLines.push(mapping.generatedLine));
  assert.deepEqual(generatedLines, [10_000_001]);
  assert.throws(() => new SourceMapConsumer(indexedMap(1, 0, atLimit)),
    /including offsets of nested sections/);
});

test("nested source lookup reads the underlying sources only once", () => {
  let map = basicMap();
  for (let depth = 0; depth < 12; depth++) map = indexedMap(0, 0, map);
  const consumer = new SourceMapConsumer(map);
  let inner = consumer;
  for (let depth = 0; depth < 12; depth++) inner = inner._sections[0].consumer;
  const sources = inner.sources;
  let reads = 0;
  Object.defineProperty(inner, "sources", { get() { reads++; return sources; } });
  assert.deepEqual(consumer.sources, ["input.js"]);
  assert.equal(reads, 1);
});

test("ordinary nested source maps still flatten and round-trip correctly", () => {
  const consumer = new SourceMapConsumer(indexedMap(2, 0, indexedMap(3)));
  const generator = new SourceMapGenerator();
  consumer.eachMapping((mapping) => generator.addMapping({
    generated: { line: mapping.generatedLine, column: mapping.generatedColumn },
    original: { line: mapping.originalLine, column: mapping.originalColumn },
    source: mapping.source,
  }));
  for (const source of consumer.sources) {
    generator.setSourceContent(source, consumer.sourceContentFor(source));
  }
  const restored = new SourceMapConsumer(generator.toJSON());
  assert.deepEqual(restored.originalPositionFor({ line: 6, column: 0 }), {
    source: "input.js", line: 1, column: 0, name: null,
  });
  assert.equal(restored.sourceContentFor("input.js"), "const value = 1;");
});
