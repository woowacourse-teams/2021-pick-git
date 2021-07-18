import { useMutation } from "react-query";
import { CommentAddData, CommentData } from "../../@types";
import storage from "../../storage/storage";
import { requestAddPostComment, requestDeletePostComment } from "../requests/comments";

export const useAddPostCommentMutation = () => {
  const { getAccessToken } = storage();

  return useMutation((commentAddData: CommentAddData) => requestAddPostComment(commentAddData, getAccessToken()));
};

export const useDeletePostCommentMutation = () => {
  const { getAccessToken } = storage();

  return useMutation((commentId: CommentData["commentId"]) => requestDeletePostComment(commentId, getAccessToken()));
};
