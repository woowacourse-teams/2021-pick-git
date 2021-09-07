import { InfiniteData, QueryKey, useQueryClient } from "react-query";
import { CommentData, Post } from "../../@types";

import {
  useAddPostLikeMutation,
  useDeletePostLikeMutation,
  useAddPostCommentMutation,
  useDeletePostCommentMutation,
  useDeletePostMutation,
} from "../queries";

const useFeedMutation = (queryKey: QueryKey) => {
  const { mutateAsync: mutateDeletePostLike, isLoading: isDeletePostLikeLoading } = useDeletePostLikeMutation();
  const { mutateAsync: mutateAddPostLike, isLoading: isAddPostLikeLoading } = useAddPostLikeMutation();
  const { mutateAsync: mutateDeletePost, isLoading: isDeletePostLoading } = useDeletePostMutation();
  const { mutateAsync: mutateAddComment } = useAddPostCommentMutation();
  const { mutateAsync: mutateDeleteComment } = useDeletePostCommentMutation();
  const queryClient = useQueryClient();

  const infinitePostsData = queryClient.getQueryData<InfiniteData<Post[]>>(queryKey) as InfiniteData<Post[]>;

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

  const addPostLike = async (postId: Post["id"]) => {
    const newPostsPages = [...infinitePostsData.pages];
    const targetPost = getTargetPost(postId, newPostsPages);

    if (!targetPost) {
      return;
    }

    const { liked, likesCount } = await mutateAddPostLike(targetPost.id);
    targetPost.liked = liked;
    targetPost.likesCount = likesCount;

    setPostsPages(newPostsPages);
  };

  const addPostComment = async (postId: Post["id"], commentValue: CommentData["content"]) => {
    const newPostsPages = [...infinitePostsData.pages];
    const targetPost = getTargetPost(postId, newPostsPages);

    if (!targetPost) {
      return;
    }

    const newComment = await mutateAddComment({ postId, commentContent: commentValue });
    targetPost.comments.push(newComment);

    setPostsPages(newPostsPages);
  };

  const deletePost = async (postId: Post["id"]) => {
    await mutateDeletePost(postId);

    const newPostsPages = infinitePostsData.pages.map((postPage) => postPage.filter((post) => post.id !== postId));

    setPostsPages(newPostsPages);
  };

  const deletePostLike = async (postId: Post["id"]) => {
    const newPostsPages = [...infinitePostsData.pages];
    const targetPost = getTargetPost(postId, newPostsPages);

    if (!targetPost) {
      return;
    }

    const { liked, likesCount } = await mutateDeletePostLike(targetPost.id);
    targetPost.liked = liked;
    targetPost.likesCount = likesCount;

    setPostsPages(newPostsPages);
  };

  return {
    setPostsPages,
    addPostLike,
    addPostComment,
    deletePost,
    deletePostLike,
    isDeletePostLikeLoading,
    isAddPostLikeLoading,
    isDeletePostLoading,
  };
};

export default useFeedMutation;
