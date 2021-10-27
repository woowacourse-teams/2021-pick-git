import { rest } from "msw";
import { setupServer } from "msw/node";
import { UNAUTHORIZED_TOKEN_ERROR, VALID_ACCESS_TOKEN } from "./shared";

export const TARGET_USERNAME = "target_user_name";
export const PREV_FOLLOWER_COUNT = 3;
export const PREV_FOLLOWING = false;
export const ADDED_FOLLOWER_COUNT = PREV_FOLLOWER_COUNT + 1;
export const DELETED_FOLLOWER_COUNT = PREV_FOLLOWER_COUNT - 1;

export const mockQuerySetter = jest.fn();

const URL = {
  FOLLOWINGS: "http://localhost:3000/api/profiles/:username/followings",
};

export const followServer = setupServer(
  rest.post(URL.FOLLOWINGS, (req, res, ctx) => {
    return req.headers.has("Authorization") && req.headers.get("Authorization") === `Bearer ${VALID_ACCESS_TOKEN}`
      ? res(ctx.json({ followerCount: ADDED_FOLLOWER_COUNT, following: true }))
      : res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
  }),
  rest.delete(URL.FOLLOWINGS, (req, res, ctx) => {
    return req.headers.has("Authorization") && req.headers.get("Authorization") === `Bearer ${VALID_ACCESS_TOKEN}`
      ? res(ctx.json({ followerCount: DELETED_FOLLOWER_COUNT, following: false }))
      : res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
  })
);
