import type { Config } from "@jest/types";

const config: Config.InitialOptions = {
  verbose: true,
  roots: ["<rootDir>/src"],
  testMatch: ["**/?(*.)+(test).+(ts|tsx)"],
  transform: {
    "^.+\\.(ts|tsx)$": "ts-jest",
  },
  setupFilesAfterEnv: ["<rootDir>/jestSetup.ts"],
  testEnvironment: "jsdom",
};

export default config;
