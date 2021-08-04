import { AxiosError } from "axios";
import { useMutation } from "react-query";
import { CommentAddData, CommentData, ErrorResponse, Post } from "../../@types";
import { getAccessToken } from "../../storage/storage";
import { requestAddPostComment, requestDeletePostComment } from "../requests/comments";

export const useAddPostCommentMutation = () => {
  return useMutation<CommentData, AxiosError<ErrorResponse>, CommentAddData>(async (commentAddData) => {
    const response = await requestAddPostComment(commentAddData, getAccessToken());
    return response.data;
  });
};

export const useDeletePostCommentMutation = () => {
  return useMutation<void, AxiosError<ErrorResponse>, CommentData["id"]>((commentId) =>
    requestDeletePostComment(commentId, getAccessToken())
  );
};
