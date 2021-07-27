import { AxiosError } from "axios";
import { QueryFunction, useInfiniteQuery, useMutation } from "react-query";

import { ErrorResponse, Post } from "../../@types";
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

const userPostsQueryFunction: QueryFunction<Post[]> = async ({ queryKey, pageParam = 0 }) => {
  const [, { isMyFeed, username }] = queryKey as UserPostsQueryKey;
  const accessToken = getAccessToken();

  if (isMyFeed) {
    if (!accessToken) throw Error("no accessToken");

    return await requestGetMyFeedPosts(pageParam, accessToken);
  } else {
    if (!username) throw Error("no username");

    return await requestGetUserFeedPosts(username, pageParam, accessToken);
  }
};

export const useHomeFeedPostsQuery = () => {
  return useInfiniteQuery<Post[], AxiosError<ErrorResponse>>(
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
  return useInfiniteQuery<Post[], AxiosError<ErrorResponse>>(
    [QUERY.GET_USER_FEED_POSTS, { isMyFeed, username }],
    userPostsQueryFunction,
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
    }
  );
};

export const useAddPostLikeMutation = () => {
  return useMutation((postId: Post["id"]) => requestAddPostLike(postId, getAccessToken()));
};

export const useDeletePostLikeMutation = () => {
  return useMutation((postId: Post["id"]) => requestDeletePostLike(postId, getAccessToken()));
};
