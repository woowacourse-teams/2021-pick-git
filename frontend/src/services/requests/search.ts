import axios from "axios";

import { Post, UserItem } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";

export const requestGetSearchUserResult = async (keyword: string, pageParam: number, accessToken: string | null) => {
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
  const response = await axios.get<UserItem[]>(
    API_URL.SEARCH_USER(keyword, pageParam, LIMIT.SEARCH_RESULT_COUNT_PER_FETCH),
    config
  );

  return response.data;
};

export const requestGetSearchPostResult = async (
  type: string | null,
  keyword: string,
  pageParam: number,
  accessToken: string | null
) => {
  if (!type) {
    return null;
  }

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
  const response = await axios.get<Post[]>(
    API_URL.SEARCH_POST(type, keyword, pageParam, LIMIT.FEED_COUNT_PER_FETCH),
    config
  );

  return response.data;
};
