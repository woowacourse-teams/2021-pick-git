import axios from "axios";

import { SearchResult } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";

export const requestGetSearchResult = async (keyword: string, pageParam: number, accessToken: string | null) => {
  if (keyword === "") {
    return null;
  }

  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<SearchResult>(
    // API_URL.SEARCH(keyword, pageParam, LIMIT.SEARCH_RESULT_COUNT_PER_FETCH),
    "http://localhost:3001/api/search",
    config
  );

  return response.data;
};
