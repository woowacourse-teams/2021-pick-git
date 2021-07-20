import { useState } from "react";
import { UseInfiniteQueryResult, useQueryClient } from "react-query";
import { CommentData, Post } from "../../@types";
import { QUERY } from "../../constants/queries";

import {
  useAddPostLikeMutation,
  useDeletePostLikeMutation,
  useAddPostCommentMutation,
  useDeletePostCommentMutation,
} from "../queries";

const useFeed = () => {
  const queryClient = useQueryClient();
  const [commentValue, setCommentValue] = useState("");
  const { mutateAsync: mutateDeletePostLike } = useDeletePostLikeMutation();
  const { mutateAsync: mutateAddPostLike } = useAddPostLikeMutation();
  const { mutateAsync: mutateAddComment } = useAddPostCommentMutation();
  const { mutateAsync: mutateDeleteComment } = useDeletePostCommentMutation();

  const deletePostLike = async (post: Post) => {
    try {
      await mutateDeletePostLike(post.postId);
    } catch (error) {
      alert(error.message);
    }
  };

  const addPostLike = async (post: Post) => {
    try {
      await mutateAddPostLike(post.postId);
    } catch (error) {
      alert(error.message);
    }
  };

  const addComment = async (postId: Post["postId"], commentContent: CommentData["content"]) => {
    try {
      await mutateAddComment({ postId, commentContent });
    } catch (error) {
      alert(error.message);
    }
  };

  const deleteComment = async (postId: Post["postId"]) => {
    try {
      await mutateDeleteComment(postId);
    } catch (error) {
      alert(error.message);
    }
  };

  const setPosts = (posts: Post[]) => {
    queryClient.setQueryData(QUERY.GET_HOME_FEED_POSTS, posts);
  };

  return {
    // postsPages: data?.pages,
    // isLoading,
    // error,
    // isFetchingNextPage,
    // hasNextPage,
    // fetchNextPage,
    commentValue,
    setCommentValue,
    deletePostLike,
    addPostLike,
    setPosts,
    addComment,
    deleteComment,
  };
};

export default useFeed;
