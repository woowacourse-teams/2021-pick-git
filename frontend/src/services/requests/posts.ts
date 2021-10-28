import axios from "axios";

import { FeedFilterOption, Post, PostEditData, PostUploadData } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";
import { customError } from "../../utils/error";

export const requestGetHomeFeedPosts = async (
  pageParam: number,
  accessToken: string | null,
  type?: FeedFilterOption
) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};

  const response = await axios.get<Post[]>(API_URL.POSTS(pageParam, LIMIT.FEED_COUNT_PER_FETCH, type), config);

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

export const requestAddPostLike = async (postId: Post["id"], accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.put<{ likesCount: number; liked: boolean }>(API_URL.POST_LIKES(postId), null, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestDeletePost = async (postId: Post["id"], accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  await axios.delete(API_URL.DELETE_POST(postId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};

export const requestDeletePostLike = async (postId: Post["id"], accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.delete<{ likesCount: number; liked: boolean }>(API_URL.POST_LIKES(postId), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestAddPost = async (
  username: string,
  { files, githubRepositoryName, tags, content }: PostUploadData,
  accessToken: string | null
) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const formData = new FormData();
  files.forEach((file) => formData.append("images", file));
  formData.append("githubRepoUrl", `https://github.com/${username}/${githubRepositoryName}`);
  formData.append("content", content);
  formData.append("tags", tags.join(","));

  await axios.post(API_URL.ADD_POST, formData, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
};

export const requestEditPost = async ({ postId, tags, content }: PostEditData, accessToken: string | null) => {
  if (!accessToken) {
    throw Error("no accessToken");
  }

  await axios.put(
    API_URL.EDIT_POST(postId),
    { tags, content },
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );
};

export const requestGetPost = async (postId: number) => {
  const response = await axios.get<Post>(API_URL.POST(postId));

  return response.data;
};
