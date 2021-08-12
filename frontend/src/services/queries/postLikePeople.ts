import { AxiosError } from "axios";
import { useQuery } from "react-query";
import { ErrorResponse, Post, UserItem } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestGetPostLikePeople } from "../requests/postLikePeople";

export const usePostLikePeopleQuery = (postId: Post["id"]) => {
  return useQuery<UserItem[], AxiosError<ErrorResponse>, UserItem[], [string, number]>(
    [QUERY.GET_POST_LIKE_PEOPLE, postId],
    async ({ queryKey }) => {
      const [, postIdParam] = queryKey;
      return requestGetPostLikePeople(postIdParam, getAccessToken());
    }
  );
};
