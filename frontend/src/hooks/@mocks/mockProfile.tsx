import { rest } from "msw";
import { setupServer } from "msw/node";

import { ProfileData } from "../../@types";
import { DEFAULT_PROFILE_QUERY_DATA, INVALID_ACCESS_TOKEN, UNAUTHORIZED_TOKEN_ERROR } from "./shared";

export const MY_PROFILE_DATA: ProfileData = {
  ...DEFAULT_PROFILE_QUERY_DATA,
  following: null,
};
export const USER_PROFILE_DATA: ProfileData = {
  ...DEFAULT_PROFILE_QUERY_DATA,
  name: "chris",
};

export const mockHistoryPush = jest.fn();

jest.mock("react-router-dom", () => {
  const originalModule = jest.requireActual("react-router-dom");

  return {
    __esModule: true,
    ...originalModule,
    useHistory: jest.fn(() => {
      return {
        push: mockHistoryPush,
      };
    }),
  };
});

const URL = {
  MY_PROFILE: "http://localhost:3000/api/profiles/me",
  USER_PROFILE: "http://localhost:3000/api/profiles/:username",
};

export const profileServer = setupServer(
  rest.get(URL.MY_PROFILE, (req, res, ctx) => {
    if (req.headers.get("Authorization") === `Bearer ${INVALID_ACCESS_TOKEN}`) {
      return res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
    }

    return res(ctx.json(MY_PROFILE_DATA));
  }),
  rest.get(URL.USER_PROFILE, (req, res, ctx) => {
    if (req.headers.get("Authorization") === `Bearer ${INVALID_ACCESS_TOKEN}`) {
      return res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
    }

    return res(ctx.json(USER_PROFILE_DATA));
  })
);
