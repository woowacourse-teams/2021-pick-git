import axios from "axios";

import { Post } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetHomeFeedPosts = async (accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<Post[]>(API_URL.POSTS, config);

  return response.data;
};

export const requestGetMyFeedPosts = async (accessToken: string) => {
  const response = await axios.get<Post[]>(API_URL.MY_POSTS, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestGetUserFeedPosts = async (userName: string, accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<Post[]>(API_URL.USER_POSTS(userName), config);

  return response.data;
};

export const requestAddPostLike = async (postId: string, accessToken: string | null) => {
  if (!accessToken) {
    throw Error("no accessToken");
  }

  const response = await axios.post<{ likesCount: number; isLiked: boolean }>(API_URL.POSTS_LIKES(postId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestDeletePostLike = async (postId: string, accessToken: string | null) => {
  if (!accessToken) {
    throw Error("no accessToken");
  }

  const response = await axios.delete<{ likesCount: number; isLiked: boolean }>(API_URL.POSTS_LIKES(postId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};
