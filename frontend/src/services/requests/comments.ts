import axios from "axios";
import { CommentAddData, CommentData, Post } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";
import { customError } from "../../utils/error";

export const requestGetPostComments = async (postId: Post["id"], pageParam: number, accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};

  const response = await axios.get<CommentData[]>(
    API_URL.POST_COMMENTS(postId, pageParam, LIMIT.COMMENTS_COUNT_PER_FETCH),
    config
  );

  return response.data;
};

export const requestAddPostComment = async ({ postId, commentContent }: CommentAddData, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.post(
    API_URL.POST_COMMENT(postId),
    { content: commentContent },
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  return response.data;
};

export const requestDeletePostComment = async (
  postId: Post["id"],
  commentId: CommentData["id"],
  accessToken: string | null
) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  await axios.delete(API_URL.POST_COMMENT(postId, commentId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};
