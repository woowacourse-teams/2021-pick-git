import axios from "axios";
import { CommentAddData, CommentData, Post } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestAddPostComment = async ({ postId, commentContent }: CommentAddData, accessToken: string | null) => {
  return await axios.post(
    API_URL.POSTS_COMMENTS(postId),
    { content: commentContent },
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );
};

export const requestDeletePostComment = async (commentId: CommentData["id"], accessToken: string | null) => {
  await axios.delete(API_URL.POSTS_COMMENTS(commentId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};
