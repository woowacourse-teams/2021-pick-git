import { AxiosError } from "axios";
import { QueryFunction, useInfiniteQuery, useMutation, useQuery } from "react-query";

import { Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import {
  requestAddPostLike,
  requestGetHomeFeedPosts,
  requestDeletePostLike,
  requestGetMyFeedPosts,
  requestGetUserFeedPosts,
} from "../requests";

type UserPostsQueryKey = readonly [
  typeof QUERY.GET_USER_FEED_POSTS,
  {
    isMyFeed: boolean;
    username: string | null;
  }
];

const userPostsQueryFunction: QueryFunction<Post[]> = async ({ queryKey }) => {
  const [, { isMyFeed, username }] = queryKey as UserPostsQueryKey;
  const accessToken = getAccessToken();

  if (isMyFeed) {
    if (!accessToken) throw Error("no accessToken");

    return await requestGetMyFeedPosts(0, accessToken);
  } else {
    return await requestGetUserFeedPosts(username as string, 0, accessToken);
  }
};

export const useHomeFeedPostsQuery = () => {
  return useInfiniteQuery(
    QUERY.GET_HOME_FEED_POSTS,
    async ({ pageParam = 0 }) => {
      return await requestGetHomeFeedPosts(pageParam, getAccessToken());
    },
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
    }
  );
};

export const useUserPostsQuery = (isMyFeed: boolean, username: string | null) => {
  return useQuery<Post[], AxiosError<Post[]>>(
    [QUERY.GET_USER_FEED_POSTS, { isMyFeed, username }],
    userPostsQueryFunction
  );
};

export const useAddPostLikeMutation = () => {
  return useMutation((postId: Post["postId"]) => requestAddPostLike(postId, getAccessToken()));
};

export const useDeletePostLikeMutation = () => {
  return useMutation((postId: Post["postId"]) => requestDeletePostLike(postId, getAccessToken()));
};
