import axios from "axios";
import { CommentAddData, Post } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestAddPostComment = async ({ postId, commentContent }: CommentAddData, accessToken: string | null) => {
  await axios.post(API_URL.POSTS_COMMENTS(postId), commentContent, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};

export const requestDeletePostComment = async (postId: Post["id"], accessToken: string | null) => {
  await axios.delete(API_URL.POSTS_COMMENTS(postId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};
