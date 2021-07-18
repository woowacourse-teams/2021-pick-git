import axios from "axios";

import { Post, PostAddFormData } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";

export const requestGetHomeFeedPosts = async (pageParam: number, accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<Post[]>(API_URL.POSTS(pageParam, LIMIT.FEED_COUNT_PER_FETCH), config);

  return response.data;
};

export const requestGetMyFeedPosts = async (pageParam: number, accessToken: string) => {
  const response = await axios.get<Post[]>(API_URL.MY_POSTS(pageParam, LIMIT.FEED_COUNT_PER_FETCH), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestGetUserFeedPosts = async (username: string, pageParam: number, accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<Post[]>(API_URL.USER_POSTS(username, pageParam, LIMIT.FEED_COUNT_PER_FETCH), config);

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

export const requestAddPost = async (
  username: string,
  { files, githubRepositoryName, tags, content }: PostAddFormData,
  accessToken: string | null
) => {
  if (!accessToken) {
    throw Error("no accessToken");
  }

  const formData = new FormData();
  files.forEach((file) => formData.append("images", file));
  formData.append("githubRepoUrl", `https://github.com/${username}/${githubRepositoryName}`);
  formData.append("tags", JSON.stringify(tags));
  formData.append("content", content);

  await axios.post(API_URL.ADD_POSTS, formData, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};
