import { AxiosError } from "axios";
import { QueryFunction, useInfiniteQuery, useMutation } from "react-query";

import { ErrorResponse, FeedFilterOption, Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { customError } from "../../utils/error";
import {
  requestAddPostLike,
  requestGetHomeFeedPosts,
  requestDeletePostLike,
  requestGetMyFeedPosts,
  requestGetUserFeedPosts,
  requestDeletePost,
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
    if (!accessToken) throw customError.noAccessToken;

    return await requestGetMyFeedPosts(pageParam, accessToken);
  } else {
    if (!username) return Promise.resolve([]);

    return await requestGetUserFeedPosts(username, pageParam, accessToken);
  }
};

export const useHomeFeedPostsQuery = (feedFilterOption: FeedFilterOption) => {
  return useInfiniteQuery<Post[], AxiosError<ErrorResponse>>(
    [QUERY.GET_HOME_FEED_POSTS],
    async ({ pageParam = 0 }) => {
      return await requestGetHomeFeedPosts(pageParam, feedFilterOption === "all" ? null : getAccessToken());
    },
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
      cacheTime: 0,
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
      suspense: true,
    }
  );
};

export const useAddPostLikeMutation = () => {
  return useMutation((postId: Post["id"]) => requestAddPostLike(postId, getAccessToken()));
};

export const useDeletePostMutation = () => {
  return useMutation<void, AxiosError<ErrorResponse>, Post["id"]>((postId: Post["id"]) =>
    requestDeletePost(postId, getAccessToken())
  );
};

export const useDeletePostLikeMutation = () => {
  return useMutation((postId: Post["id"]) => requestDeletePostLike(postId, getAccessToken()));
};
