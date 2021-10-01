import { AxiosError } from "axios";

import { QueryKey, useInfiniteQuery } from "react-query";
import { ErrorResponse, Post, UserItem } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetSearchUserResult, requestGetSearchPostResult } from "../requests";

export const useSearchUserResultQuery = (keyword: string, activated: boolean) =>
  useInfiniteQuery<UserItem[] | null, AxiosError<ErrorResponse>>(
    [QUERY.GET_SEARCH_USER_RESULT, { keyword }],
    async ({ pageParam = 0 }) =>
      activated ? await requestGetSearchUserResult(keyword, pageParam, getAccessToken()) : Promise.resolve(null),
    {
      cacheTime: 0,
      getNextPageParam: (_, pages) => pages.length,
    }
  );

export const useSearchPostResultQuery = (
  type: string | null,
  keyword: string,
  queryKey: QueryKey,
  activated?: boolean
) =>
  useInfiniteQuery<Post[] | null, AxiosError<ErrorResponse>>(
    queryKey,
    async ({ pageParam = 0 }) =>
      activated ?? true
        ? await requestGetSearchPostResult(type, keyword, pageParam, getAccessToken())
        : Promise.resolve(null),
    {
      cacheTime: 0,
      getNextPageParam: (_, pages) => pages.length,
    }
  );
