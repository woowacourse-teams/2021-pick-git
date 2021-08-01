import { QueryKey, useQueryClient } from "react-query";
import { CommentData, Post } from "../../@types";

import {
  useAddPostLikeMutation,
  useDeletePostLikeMutation,
  useAddPostCommentMutation,
  useDeletePostCommentMutation,
  useDeletePostMutation,
} from "../queries";

const useFeedMutation = (queryKey: QueryKey) => {
  const { mutateAsync: mutateDeletePostLike } = useDeletePostLikeMutation();
  const { mutateAsync: mutateAddPostLike } = useAddPostLikeMutation();
  const { mutateAsync: mutateDeletePost } = useDeletePostMutation();
  const { mutateAsync: mutateAddComment } = useAddPostCommentMutation();
  const { mutateAsync: mutateDeleteComment } = useDeletePostCommentMutation();
  const queryClient = useQueryClient();

  const setPosts = (posts: Post[]) => {
    queryClient.setQueryData<Post[]>(queryKey, posts);
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
    mutateAddPostLike,
    mutateDeletePostLike,
    mutateAddComment,
    mutateDeletePost,
    deleteComment,
  };
};

export default useFeedMutation;
