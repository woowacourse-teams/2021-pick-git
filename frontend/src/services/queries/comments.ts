import { useMutation } from "react-query";
import { CommentAddData, CommentData } from "../../@types";
import { getAccessToken } from "../../storage/storage";
import { requestAddPostComment, requestDeletePostComment } from "../requests/comments";

export const useAddPostCommentMutation = () => {
  return useMutation((commentAddData: CommentAddData) => requestAddPostComment(commentAddData, getAccessToken()));
};

export const useDeletePostCommentMutation = () => {
  return useMutation((commentId: CommentData["commentId"]) => requestDeletePostComment(commentId, getAccessToken()));
};
