import { rest } from "msw";
import { setupServer } from "msw/node";

import { UserItem } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { EMPTY_PAGE, INVALID_ACCESS_TOKEN, MOCK_USER, UNAUTHORIZED_TOKEN_ERROR } from "./shared";

export const SEARCH_TYPE = "tags";
export const SEARCH_RESULT_MAX_PAGE_LENGTH = 3;
export const SEARCH_RESULT_PAGES: UserItem[][] = Array(SEARCH_RESULT_MAX_PAGE_LENGTH).fill(
  Array(LIMIT.SEARCH_RESULT_COUNT_PER_FETCH).fill(MOCK_USER)
);
export const SEARCH_POST_KEYWORD = "search_user_keyword";
export const EMPTY_RESULT_KEYWORD = "empty_user_keyword";

const URL = {
  SEARCH_POST: "http://localhost:3000/api/search/posts",
};

export const searchPostDataServer = setupServer(
  rest.get(URL.SEARCH_POST, (req, res, ctx) => {
    if (req.headers.get("Authorization") === `Bearer ${INVALID_ACCESS_TOKEN}`) {
      return res(ctx.status(401), ctx.json({ errorCode: UNAUTHORIZED_TOKEN_ERROR }));
    }

    const pageParam = Number(req.url.searchParams.get("page") ?? 0);
    const keywordParam = req.url.searchParams.get("keyword");
    const currentPage = keywordParam === EMPTY_RESULT_KEYWORD ? EMPTY_PAGE : SEARCH_RESULT_PAGES;

    return res(ctx.json(currentPage[pageParam] ?? []));
  })
);
