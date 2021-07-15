import { useMutation } from "react-query";
import { CommentAddData, CommentData } from "../../@types";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestAddPostComment, requestDeletePostComment } from "../requests/comments";

export const useAddPostCommentMutation = () => {
  const { accessToken } = useLocalStorage();

  return useMutation((commentAddData: CommentAddData) => requestAddPostComment(commentAddData, accessToken));
};

export const useDeletePostCommentMutation = () => {
  const { accessToken } = useLocalStorage();

  return useMutation((commentId: CommentData["commentId"]) => requestDeletePostComment(commentId, accessToken));
};
