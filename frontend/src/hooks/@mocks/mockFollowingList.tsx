import { rest } from "msw";
import { setupServer } from "msw/node";
import { EMPTY_PAGE, INVALID_ACCESS_TOKEN, MOCK_USER, UNAUTHORIZED_TOKEN_ERROR } from "./shared";

import { UserItem } from "../../@types/index";
import { LIMIT } from "../../constants/limits";

export const USER_WITH_FOLLOWINGS = "user_with_followings";
export const USER_WITH_NO_FOLLOWINGS = "user_with_no_followings";
export const FOLLOWING_LIST_MAX_PAGE_LENGTH = 3;
export const FOLLOWING_LIST_PAGES: UserItem[][] = Array(FOLLOWING_LIST_MAX_PAGE_LENGTH).fill(
  Array(LIMIT.SEARCH_RESULT_COUNT_PER_FETCH).fill(MOCK_USER)
);

const URL = {
  GET_FOLLOWING_LIST: "http://localhost:3000/api/profiles/:username/followings",
};

export const followingListServer = setupServer(
  rest.get(URL.GET_FOLLOWING_LIST, (req, res, ctx) => {
    if (req.headers.get("Authorization") === `Bearer ${INVALID_ACCESS_TOKEN}`) {
      return res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
    }

    const pageParam = Number(req.url.searchParams.get("page") ?? 0);
    const currentPage = req.params.username === USER_WITH_FOLLOWINGS ? FOLLOWING_LIST_PAGES : EMPTY_PAGE;

    return res(ctx.json(currentPage[pageParam] ?? []));
  })
);
