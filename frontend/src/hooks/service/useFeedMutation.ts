import { useContext } from "react";
import { InfiniteData, QueryKey, useQueryClient } from "react-query";
import { Post } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import SnackBarContext from "../../contexts/SnackbarContext";

import { useAddPostLikeMutation, useDeletePostLikeMutation, useDeletePostMutation } from "../../services/queries";

const useFeedMutation = (queryKeyList: QueryKey[]) => {
  const { mutateAsync: mutateDeletePostLike } = useDeletePostLikeMutation();
  const { mutateAsync: mutateAddPostLike } = useAddPostLikeMutation();
  const { mutateAsync: mutateDeletePost, isLoading: isDeletePostLoading } = useDeletePostMutation();
  const queryClient = useQueryClient();

  const infinitePostsDataList = queryKeyList.map((queryKey) =>
    queryClient.getQueryData<InfiniteData<Post[]>>(queryKey)
  ) as InfiniteData<Post[]>[];
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const setPostsPages = (postsPages: Post[][], queryKey: QueryKey) => {
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
    infinitePostsDataList.forEach((infinitePostsData, index) => {
      const newPostsPages = [...infinitePostsData.pages];
      const targetPost = getTargetPost(postId, newPostsPages);

      if (targetPost) {
        targetPost.liked = state.liked;
        targetPost.likesCount = state.likesCount;

        setPostsPages(newPostsPages, queryKeyList[index]);
      }
    });
  };

  const addPostLike = async (postId: Post["id"]) => {
    let addingLikePost: Post; 

    infinitePostsDataList.forEach(async (infinitePostsData, index) => {
      const newPostsPages = [...infinitePostsData.pages];
      const targetPost = getTargetPost(postId, newPostsPages);

      if (targetPost) {

        targetPost.liked = true;
        targetPost.likesCount += 1;

        setPostsPages(newPostsPages, queryKeyList[index]);
        addingLikePost = targetPost;
      }
    });

    const prevPostLiked = false;
    const prevPostLikesCount = addingLikePost!.likesCount - 1;

    try {
      const { liked, likesCount } = await mutateAddPostLike(postId);

      if (liked === prevPostLiked || likesCount === prevPostLikesCount) {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
        setPostLike(postId, { liked: prevPostLiked, likesCount: prevPostLikesCount });
      }
    } catch (error) {
      pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      setPostLike(postId, { liked: prevPostLiked, likesCount: prevPostLikesCount });
    }
  };

  const deletePost = async (postId: Post["id"]) => {
    await mutateDeletePost(postId);

    infinitePostsDataList.forEach((infinitePostsData, index) => {
      const newPostsPages = infinitePostsData.pages.map((postPage) => postPage.filter((post) => post.id !== postId));

      setPostsPages(newPostsPages, queryKeyList[index]);
    });
  };

  const deletePostLike = async (postId: Post["id"]) => {
    let deletingPost: Post; 

    infinitePostsDataList.forEach(async (infinitePostsData, index) => {
      const newPostsPages = [...infinitePostsData.pages];
      const targetPost = getTargetPost(postId, newPostsPages);

      if (targetPost) {

        targetPost.liked = false;
        if (targetPost.likesCount > 0) {
          targetPost.likesCount -= 1;
        }

        setPostsPages(newPostsPages, queryKeyList[index]);
        deletingPost = targetPost;
      }
    });

    const prevPostLiked = true;
    const prevPostLikesCount = deletingPost!.likesCount + 1;

    try {
      const { liked, likesCount } = await mutateDeletePostLike(postId);

      if (liked === prevPostLiked || likesCount === prevPostLikesCount) {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
        setPostLike(postId, { liked: prevPostLiked, likesCount: prevPostLikesCount });
      }
    } catch (error) {
      pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      setPostLike(postId, { liked: prevPostLiked, likesCount: prevPostLikesCount });
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
