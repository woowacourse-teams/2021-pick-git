import { AxiosError } from "axios";
import { useInfiniteQuery } from "react-query";
import { ErrorResponse, Post, UserItem } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetPostLikePeople } from "../requests/postLikePeople";

export const usePostLikePeopleQuery = (postId: Post["id"]) => {
  return useInfiniteQuery<UserItem[], AxiosError<ErrorResponse>, UserItem[], [string, number]>(
    [QUERY.GET_POST_LIKE_PEOPLE, postId],
    async ({ pageParam = 0, queryKey }) => {
      const [, postIdParam] = queryKey;
      return requestGetPostLikePeople(postIdParam, pageParam, getAccessToken());
    },
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
      cacheTime: 0,
    }
  );
};
