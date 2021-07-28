import { AxiosError } from "axios";

import { useInfiniteQuery } from "react-query";
import { ErrorResponse, SearchResult } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetSearchResult } from "../requests/search";

export const useSearchResultQuery = (keyword: string) => {
  const accessToken = getAccessToken();

  return useInfiniteQuery<SearchResult | null, AxiosError<ErrorResponse>>(
    [QUERY.GET_SEARCH_RESULT, { keyword }],
    async ({ pageParam = 0 }) => await requestGetSearchResult(keyword, pageParam, accessToken),
    {
      cacheTime: 0,
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
    }
  );
};
