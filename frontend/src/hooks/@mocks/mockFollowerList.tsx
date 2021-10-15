import { rest } from "msw";
import { setupServer } from "msw/node";
import { EMPTY_PAGE, INVALID_ACCESS_TOKEN, MOCK_USER, UNAUTHORIZED_TOKEN_ERROR } from "./shared";

import { UserItem } from "../../@types/index";
import { LIMIT } from "../../constants/limits";

export const USER_WITH_FOLLOWERS = "user_with_followers";
export const USER_WITH_NO_FOLLOWERS = "user_with_no_followers";
export const FOLLOWER_LIST_MAX_PAGE_LENGTH = 3;
export const FOLLOWER_LIST_PAGES: UserItem[][] = Array(FOLLOWER_LIST_MAX_PAGE_LENGTH).fill(
  Array(LIMIT.SEARCH_RESULT_COUNT_PER_FETCH).fill(MOCK_USER)
);

const URL = {
  GET_FOLLOWER_LIST: "http://localhost:3000/api/profiles/:username/followers",
};

export const followerListServer = setupServer(
  rest.get(URL.GET_FOLLOWER_LIST, (req, res, ctx) => {
    if (req.headers.get("Authorization") === `Bearer ${INVALID_ACCESS_TOKEN}`) {
      return res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
    }

    const pageParam = Number(req.url.searchParams.get("page") ?? 0);
    const currentPage = req.params.username === USER_WITH_FOLLOWERS ? FOLLOWER_LIST_PAGES : EMPTY_PAGE;

    return res(ctx.json(currentPage[pageParam] ?? []));
  })
);
