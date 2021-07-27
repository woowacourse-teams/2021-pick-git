import { useQueryClient } from "react-query";
import { CommentData, Post } from "../../@types";
import { QUERY } from "../../constants/queries";

import {
  useAddPostLikeMutation,
  useDeletePostLikeMutation,
  useAddPostCommentMutation,
  useDeletePostCommentMutation,
} from "../queries";

const useFeedMutation = (queryKey: string) => {
  const { mutateAsync: mutateDeletePostLike } = useDeletePostLikeMutation();
  const { mutateAsync: mutateAddPostLike } = useAddPostLikeMutation();
  const { mutateAsync: mutateAddComment } = useAddPostCommentMutation();
  const { mutateAsync: mutateDeleteComment } = useDeletePostCommentMutation();
  const queryClient = useQueryClient();

  const setPosts = (posts: Post[]) => {
    queryClient.setQueryData<Post[]>(queryKey, posts);
  };

  const deletePostLike = async (post: Post) => {
    try {
      await mutateDeletePostLike(post.id);
    } catch (error) {
      alert(error.message);
    }
  };

  const addPostLike = async (post: Post) => {
    try {
      await mutateAddPostLike(post.id);
    } catch (error) {
      alert(error.message);
    }
  };

  const deleteComment = async (postId: Post["id"], commendId: CommentData["id"]) => {
    try {
      await mutateDeleteComment(commendId);
    } catch (error) {
      alert(error.message);
    }
  };

  return {
    setPosts,
    deletePostLike,
    addPostLike,
    mutateAddComment,
    deleteComment,
  };
};

export default useFeedMutation;
