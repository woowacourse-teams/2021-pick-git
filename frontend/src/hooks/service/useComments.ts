import axios from "axios";
import { useContext, useEffect } from "react";
import { InfiniteData, useQueryClient } from "react-query";
import { CommentData, Post } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import { QUERY } from "../../constants/queries";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { getAPIErrorMessage, getClientErrorMessage, handleClientError, handleHTTPError } from "../../utils/error";
import { isClientErrorCode, isHttpErrorStatus } from "../../utils/typeGuard";
import { useAddPostCommentMutation, useDeletePostCommentMutation, usePostCommentsQuery } from "../../services/queries";

const useComments = (selectedPostId: Post["id"]) => {
  const { logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const {
    data: infiniteCommentsData,
    isLoading,
    error,
    isError,
    isFetching,
    fetchNextPage,
    refetch,
  } = usePostCommentsQuery(selectedPostId);
  const queryClient = useQueryClient();

  const { mutateAsync: mutateAddComment, isLoading: isAddCommentLoading } = useAddPostCommentMutation();
  const { mutateAsync: mutateDeleteComment, isLoading: isDeleteCommentLoading } = useDeletePostCommentMutation();

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status, data } = error.response ?? {};

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, {
          unauthorized: () => {
            logout();
            queryClient.refetchQueries(QUERY.GET_POST_COMMENTS, { active: true });
          },
        });
      }

      pushSnackbarMessage(data ? getAPIErrorMessage(data.errorCode) : UNKNOWN_ERROR_MESSAGE);
    } else {
      const { message } = error;

      if (isClientErrorCode(message)) {
        handleClientError(message, {
          noAccessToken: () => {
            pushSnackbarMessage(getClientErrorMessage(message));
            logout();
          },
        });
      } else {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      }
    }
  };

  // TODO : useEffect 를 페이지로 보낼지 의논하기
  useEffect(() => {
    handleError();
  }, [error]);

  const setCommentsPages = (commentsPages: CommentData[][]) => {
    queryClient.setQueryData<InfiniteData<CommentData[]>>([QUERY.GET_POST_COMMENTS, selectedPostId], (data) => {
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

    await mutateDeleteComment({ postId, commentId });

    const newCommentsPages = [...infiniteCommentsData.pages];
    const targetPage = newCommentsPages.find((page) => page.find((comment) => comment.id === commentId));
    const targetItemIndex = targetPage?.findIndex((comment) => comment.id === commentId);

    if (targetItemIndex === -1 || targetItemIndex === undefined) {
      return;
    }

    targetPage?.splice(targetItemIndex, 1);
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
    isAddCommentLoading,
    isDeleteCommentLoading,
  };
};

export default useComments;
