import { AxiosError } from "axios";
import { QueryFunction, useMutation, useQuery } from "react-query";

import { Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
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
    accessToken: string | null;
    userName?: string;
  }
];

const userPostsQueryFunction: QueryFunction<Post[]> = async ({ queryKey }) => {
  const [, { isMyFeed, accessToken, userName }] = queryKey as UserPostsQueryKey;

  if (isMyFeed) {
    if (!accessToken) throw Error("no accessToken");

    return await requestGetMyFeedPosts(accessToken);
  } else {
    return await requestGetUserFeedPosts(userName as string, accessToken);
  }
};

export const useHomeFeedPostsQuery = () => {
  const { accessToken } = useLocalStorage();

  return useQuery<Post[], AxiosError<Post[]>>(QUERY.GET_HOME_FEED_POSTS, () => requestGetHomeFeedPosts(accessToken));
};

export const useUserPostsQuery = (isMyFeed: boolean, userName?: string) => {
  const { accessToken } = useLocalStorage();

  return useQuery<Post[], AxiosError<Post[]>>(
    [QUERY.GET_USER_FEED_POSTS, { isMyFeed, accessToken, userName }],
    userPostsQueryFunction
  );
};

export const useAddPostLikeMutation = () => {
  const { accessToken } = useLocalStorage();

  return useMutation((postId: Post["postId"]) => requestAddPostLike(postId, accessToken));
};

export const useDeletePostLikeMutation = () => {
  const { accessToken } = useLocalStorage();

  return useMutation((postId: Post["postId"]) => requestDeletePostLike(postId, accessToken));
};
