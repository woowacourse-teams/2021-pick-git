import { AxiosError } from "axios";

import { useInfiniteQuery } from "react-query";
import { ErrorResponse, SearchResultUser } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetSearchUserResult } from "../requests";

export const useSearchUserResultQuery = (keyword: string) => {
  const accessToken = getAccessToken();

  return useInfiniteQuery<SearchResultUser[] | null, AxiosError<ErrorResponse>>(
    [QUERY.GET_SEARCH_RESULT, { keyword }],
    async ({ pageParam = 0 }) => await requestGetSearchUserResult(keyword, pageParam, accessToken),
    {
      cacheTime: 0,
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
    }
  );
};
