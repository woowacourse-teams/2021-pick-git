import { AxiosError } from "axios";
import { QueryFunction, useInfiniteQuery, useMutation, useQuery } from "react-query";

import { ErrorResponse, Post } from "../../@types";
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
  requestGetPost,
} from "../requests";

type UserPostsQueryKey = readonly [
  typeof QUERY.GET_USER_FEED_POSTS,
  {
    isMyFeed: boolean;
    username: string | null;
  }
];

type PostQuery = readonly [typeof QUERY.GET_POST, number];

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

const postQueryFunction: QueryFunction<Post> = async ({ queryKey }) => {
  const [, postId] = queryKey as PostQuery;

  return await requestGetPost(postId);
};

export const useHomeFeedFollowingsPostsQuery = () => {
  return useInfiniteQuery<Post[] | null, AxiosError<ErrorResponse>>(
    QUERY.GET_HOME_FEED_POSTS("followings"),
    async ({ pageParam = 0 }) => {
      return await requestGetHomeFeedPosts(pageParam, getAccessToken(), "followings");
    },
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
      cacheTime: 0,
      refetchOnMount: "always",
    }
  );
};

export const useHomeFeedAllPostsQuery = () => {
  return useInfiniteQuery<Post[] | null, AxiosError<ErrorResponse>>(
    QUERY.GET_HOME_FEED_POSTS("all"),
    async ({ pageParam = 0 }) => {
      return await requestGetHomeFeedPosts(pageParam, getAccessToken(), "all");
    },
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
      cacheTime: 0,
      refetchOnMount: "always",
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

export const useGetPostQuery = (postId: number, activated: boolean) => {
  return useQuery<Post>(["post", postId], postQueryFunction, {
    enabled: activated,
  });
};
