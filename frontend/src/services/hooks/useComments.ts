import axios from "axios";
import { useContext, useEffect } from "react";
import { InfiniteData, useQueryClient } from "react-query";
import { CommentData, Post } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { QUERY } from "../../constants/queries";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import { useAddPostCommentMutation, useDeletePostCommentMutation, usePostCommentsQuery } from "../queries";

const useComments = (selectedPostId: Post["id"]) => {
  const { logout } = useContext(UserContext);
  const {
    data: infiniteCommentsData,
    isLoading,
    error,
    isError,
    isFetching,
    fetchNextPage,
  } = usePostCommentsQuery(selectedPostId);
  const queryClient = useQueryClient();

  const { mutateAsync: mutateAddComment } = useAddPostCommentMutation();
  const { mutateAsync: mutateDeleteComment } = useDeletePostCommentMutation();

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, {
          unauthorized: () => {
            logout();
            queryClient.refetchQueries(QUERY.GET_POST_COMMENTS, { active: true });
          },
        });
      }
    }
  };

  // TODO : useEffect 를 페이지로 보낼지 의논하기
  useEffect(() => {
    handleError();
  }, [error]);

  const setCommentsPages = (commentsPages: CommentData[][]) => {
    queryClient.setQueryData<InfiniteData<CommentData[]>>(QUERY.GET_POST_COMMENTS, (data) => {
      return {
        ...data,
        pages: commentsPages,
      } as InfiniteData<CommentData[]>;
    });
  };

  const addPostComment = async (postId: Post["id"], commentValue: CommentData["content"]) => {
    if (!infiniteCommentsData) {
      return;
    }

    const newCommentsPages = [...infiniteCommentsData.pages];
    const newComment = await mutateAddComment({ postId, commentContent: commentValue });

    const lastPage = newCommentsPages[newCommentsPages.length - 1];
    if (lastPage.length < LIMIT.COMMENTS_COUNT_PER_FETCH) {
      lastPage.push(newComment);
      setCommentsPages(newCommentsPages);
      return;
    }

    newCommentsPages.push([newComment]);
    setCommentsPages(newCommentsPages);
  };

  const deletePostComment = async (postId: Post["id"], commentId: CommentData["id"]) => {
    if (!infiniteCommentsData) {
      return;
    }

    try {
      await mutateDeleteComment({ postId, commentId });
    } catch (error) {}
    const newCommentsPages = [...infiniteCommentsData.pages];
    const lastPage = newCommentsPages[newCommentsPages.length - 1];

    if (lastPage.length > 0) {
      newCommentsPages[newCommentsPages.length - 1] = lastPage.filter((comment) => comment.id !== commentId);
    }

    setCommentsPages(newCommentsPages);
  };

  const getNextComments = () => {
    fetchNextPage();
  };

  return {
    infiniteCommentsData,
    isLoading,
    isError,
    isFetching,
    getNextComments,
    addPostComment,
    deletePostComment,
  };
};

export default useComments;
