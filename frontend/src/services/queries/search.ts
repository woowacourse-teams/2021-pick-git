import { AxiosError } from "axios";

import { QueryKey, useInfiniteQuery } from "react-query";
import { ErrorResponse, Post, SearchResultUser } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetSearchUserResult, requestGetSearchPostResult } from "../requests";

export const useSearchUserResultQuery = (keyword: string) =>
  useInfiniteQuery<SearchResultUser[] | null, AxiosError<ErrorResponse>>(
    [QUERY.GET_SEARCH_USER_RESULT, { keyword }],
    async ({ pageParam = 0 }) => await requestGetSearchUserResult(keyword, pageParam, getAccessToken()),
    {
      cacheTime: 0,
      getNextPageParam: (_, pages) => pages.length,
    }
  );

export const useSearchPostResultQuery = (type: string | null, keyword: string, queryKey: QueryKey) =>
  useInfiniteQuery<Post[] | null, AxiosError<ErrorResponse>>(
    queryKey,
    async ({ pageParam = 0 }) => await requestGetSearchPostResult(type, keyword, pageParam, getAccessToken()),
    {
      cacheTime: 0,
      getNextPageParam: (_, pages) => pages.length,
    }
  );
