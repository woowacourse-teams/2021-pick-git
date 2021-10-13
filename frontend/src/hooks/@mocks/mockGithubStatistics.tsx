import { rest } from "msw";
import { setupServer } from "msw/node";

import { INVALID_ACCESS_TOKEN, UNAUTHORIZED_TOKEN_ERROR } from "./shared";

export const GITHUB_STATS = {
  starsCount: 1000,
  commitsCount: 200,
  prsCount: 300,
  issuesCount: 400,
  reposCount: 120,
};

const URL = {
  GITHUB_STATS: "http://localhost:3000/api/profiles/:username/contributions",
};

export const githubStatisticsServer = setupServer(
  rest.get(URL.GITHUB_STATS, (req, res, ctx) => {
    if (req.headers.get("Authorization") === `Bearer ${INVALID_ACCESS_TOKEN}`) {
      return res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
    }

    return res(ctx.json(GITHUB_STATS));
  })
);
