import { defineConfig } from "vitest/config";
import angular from "@analogjs/vite-plugin-angular";
import { resolve } from "path";

export default defineConfig({
  plugins: [
    angular({
      tsconfig: "./tsconfig.json",
    }),
  ],
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["./src/test-setup.ts"],
    include: ["src/**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"],
    exclude: ["node_modules", "dist", "**/*.d.ts"],
    sequence: {
      shuffle: false,
    },
    hookTimeout: 10000,
    testTimeout: 10000,
    coverage: {
      provider: "v8",
      reporter: ["text", "json", "html"],
      exclude: [
        "src/main.ts",
        "src/app/app.component.ts",
        "src/test-setup.ts",
        "**/*.spec.ts",
        "**/*.test.ts",
      ],
    },
  },
  resolve: {
    alias: {
      "@": resolve(__dirname, "./src"),
    },
  },
});
