import { useMutation, useQuery } from "react-query";

import { Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestAddPostLike, requestGetHomeFeedPosts, requestDeletePostLike } from "../requests";

export const useHomeFeedPostsQuery = () => {
  const { accessToken } = useLocalStorage();

  return useQuery<Post[]>(QUERY.GET_HOME_FEED_POSTS, () => requestGetHomeFeedPosts(accessToken));
};

export const useAddPostLikeMutation = () => {
  const { accessToken } = useLocalStorage();

  return useMutation((postId: Post["postId"]) => requestAddPostLike(postId, accessToken));
};

export const useDeletePostLikeMutation = () => {
  const { accessToken } = useLocalStorage();

  return useMutation((postId: Post["postId"]) => requestDeletePostLike(postId, accessToken));
};
