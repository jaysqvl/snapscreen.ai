import { cpSync } from "node:fs";

// Mirror the Dockerfile's asset copies before testing its standalone server.
cpSync("public", ".next/standalone/public", { recursive: true });
cpSync(".next/static", ".next/standalone/.next/static", { recursive: true });
