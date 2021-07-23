import { AxiosError } from "axios";
import { useMutation } from "react-query";
import { CommentAddData, CommentData, ErrorResponse } from "../../@types";
import { getAccessToken } from "../../storage/storage";
import { requestAddPostComment, requestDeletePostComment } from "../requests/comments";

export const useAddPostCommentMutation = () => {
  return useMutation<void, AxiosError<ErrorResponse>, CommentAddData>((commentAddData) =>
    requestAddPostComment(commentAddData, getAccessToken())
  );
};

export const useDeletePostCommentMutation = () => {
  return useMutation<void, AxiosError<ErrorResponse>, CommentData["commentId"]>((commentId) =>
    requestDeletePostComment(commentId, getAccessToken())
  );
};
