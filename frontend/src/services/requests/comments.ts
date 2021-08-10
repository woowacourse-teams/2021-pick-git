import axios from "axios";
import { CommentAddData, CommentData, Post } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";

export const requestGetPostComments = async (postId: Post["id"], pageParam: number, accessToken: string | null) => {
  return await axios.get<CommentData[]>(API_URL.POST_COMMENTS(postId, pageParam, LIMIT.COMMENTS_COUNT_PER_FETCH), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};

export const requestAddPostComment = async ({ postId, commentContent }: CommentAddData, accessToken: string | null) => {
  return await axios.post(
    API_URL.POST_COMMENT(postId),
    { content: commentContent },
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );
};

export const requestDeletePostComment = async (
  postId: Post["id"],
  commentId: CommentData["id"],
  accessToken: string | null
) => {
  await axios.delete(API_URL.POST_COMMENT(postId, commentId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};
