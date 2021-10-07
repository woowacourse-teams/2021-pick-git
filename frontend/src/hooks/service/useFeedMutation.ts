import { useContext } from "react";
import { InfiniteData, QueryKey, useQueryClient } from "react-query";
import { CommentData, Post } from "../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../constants/messages";
import SnackBarContext from "../contexts/SnackbarContext";

import {
  useAddPostLikeMutation,
  useDeletePostLikeMutation,
  useAddPostCommentMutation,
  useDeletePostCommentMutation,
  useDeletePostMutation,
} from "../services/queries";

const useFeedMutation = (queryKey: QueryKey) => {
  const { mutateAsync: mutateDeletePostLike } = useDeletePostLikeMutation();
  const { mutateAsync: mutateAddPostLike } = useAddPostLikeMutation();
  const { mutateAsync: mutateDeletePost, isLoading: isDeletePostLoading } = useDeletePostMutation();
  const queryClient = useQueryClient();

  const infinitePostsData = queryClient.getQueryData<InfiniteData<Post[]>>(queryKey) as InfiniteData<Post[]>;
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const setPostsPages = (postsPages: Post[][]) => {
    queryClient.setQueryData<InfiniteData<Post[]>>(queryKey, (data) => {
      return {
        ...data,
        pages: postsPages,
      } as InfiniteData<Post[]>;
    });
  };

  const getTargetPost = (postId: Post["id"], postsPages: Post[][]) => {
    const targetPage = postsPages.find((page) => page.find((post) => post.id === postId));

    return targetPage?.find((post) => post.id === postId);
  };

  const setPostLike = (postId: Post["id"], state: { liked: boolean; likesCount: number }) => {
    const newPostsPages = [...infinitePostsData.pages];
    const targetPost = getTargetPost(postId, newPostsPages);

    if (targetPost) {
      targetPost.liked = state.liked;
      targetPost.likesCount = state.likesCount;

      setPostsPages(newPostsPages);
    }
  };

  const addPostLike = async (postId: Post["id"]) => {
    const targetPost = getTargetPost(postId, [...infinitePostsData.pages]);

    if (!targetPost) {
      return;
    }

    const prevLiked = targetPost?.liked;
    const prevLikesCount = targetPost?.likesCount;

    setPostLike(postId, { liked: true, likesCount: prevLikesCount + 1 });

    try {
      const { liked, likesCount } = await mutateAddPostLike(postId);

      if (liked === prevLiked || likesCount === prevLikesCount) {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
        setPostLike(postId, { liked: prevLiked, likesCount: prevLikesCount });
      }
    } catch (error) {
      pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      setPostLike(postId, { liked: prevLiked, likesCount: prevLikesCount });
    }
  };

  const deletePost = async (postId: Post["id"]) => {
    await mutateDeletePost(postId);

    const newPostsPages = infinitePostsData.pages.map((postPage) => postPage.filter((post) => post.id !== postId));

    setPostsPages(newPostsPages);
  };

  const deletePostLike = async (postId: Post["id"]) => {
    const targetPost = getTargetPost(postId, [...infinitePostsData.pages]);

    if (!targetPost) {
      return;
    }

    const prevLiked = targetPost?.liked;
    const prevLikesCount = targetPost?.likesCount;

    setPostLike(postId, { liked: false, likesCount: prevLikesCount - 1 });

    try {
      const { liked, likesCount } = await mutateDeletePostLike(targetPost.id);

      if (liked === prevLiked || likesCount === prevLikesCount) {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
        setPostLike(postId, { liked: prevLiked, likesCount: prevLikesCount });
      }
    } catch (error) {
      pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      setPostLike(postId, { liked: prevLiked, likesCount: prevLikesCount });
    }
  };

  return {
    setPostsPages,
    addPostLike,
    deletePost,
    deletePostLike,
    isDeletePostLoading,
  };
};

export default useFeedMutation;
