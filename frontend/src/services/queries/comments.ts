import { AxiosError } from "axios";
import { useInfiniteQuery, useMutation } from "react-query";
import { CommentAddData, CommentData, CommentDeleteData, ErrorResponse, Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import { getAccessToken } from "../../storage/storage";
import { requestAddPostComment, requestDeletePostComment, requestGetPostComments } from "../requests/comments";

export const usePostCommentsQuery = (postId: Post["id"]) => {
  return useInfiniteQuery<CommentData[], AxiosError<ErrorResponse>, CommentData[], [string, Post["id"]]>(
    [QUERY.GET_POST_COMMENTS, postId],
    async ({ pageParam = 0, queryKey }) => {
      const [, postIdParam] = queryKey;

      const response = await requestGetPostComments(postIdParam, pageParam, getAccessToken());

      return response.data;
    },
    {
      getNextPageParam: (_, pages) => {
        return pages.length;
      },
      cacheTime: 0,
    }
  );
};

export const useAddPostCommentMutation = () => {
  return useMutation<CommentData, AxiosError<ErrorResponse>, CommentAddData>(async (commentAddData) => {
    const response = await requestAddPostComment(commentAddData, getAccessToken());
    return response.data;
  });
};

export const useDeletePostCommentMutation = () => {
  return useMutation<void, AxiosError<ErrorResponse>, CommentDeleteData>(({ postId, commentId }) =>
    requestDeletePostComment(postId, commentId, getAccessToken())
  );
};
