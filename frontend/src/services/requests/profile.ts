import axios from "axios";

import { MutateResponseFollow, ProfileData, UserItem } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { API_URL } from "../../constants/urls";
import { customError } from "../../utils/error";

export const requestGetSelfProfile = async (accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.get<ProfileData>(API_URL.SELF_PROFILE, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestGetUserProfile = async (username: string, accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<ProfileData>(API_URL.USER_PROFILE(username), config);

  return response.data;
};

export const requestSetProfile = async (profileImage: File | null, description: string, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  if (!profileImage && !description) {
    throw Error("no data to set");
  }

  const formData = new FormData();

  formData.append("image", profileImage ?? new File([], ""));
  formData.append("description", description);

  const response = await axios.post(API_URL.SELF_PROFILE, formData, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestAddFollow = async (username: string, applyGithub: boolean, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.post<MutateResponseFollow>(API_URL.USER_PROFILE_FOLLOW(username, applyGithub), null, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestDeleteFollow = async (username: string, applyGithub: boolean, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.delete<MutateResponseFollow>(API_URL.USER_PROFILE_UNFOLLOW(username, applyGithub), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestGetFollowings = async (username: string | null, pageParam: number, accessToken: string | null) => {
  if (!username) {
    return null;
  }

  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<UserItem[]>(
    API_URL.USER_PROFILE_FOLLOWINGS(username, pageParam, LIMIT.SEARCH_RESULT_COUNT_PER_FETCH),
    config
  );

  return response.data;
};

export const requestGetFollowers = async (username: string | null, pageParam: number, accessToken: string | null) => {
  if (!username) {
    return null;
  }

  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<UserItem[]>(
    API_URL.USER_PROFILE_FOLLOWERS(username, pageParam, LIMIT.SEARCH_RESULT_COUNT_PER_FETCH),
    config
  );

  return response.data;
};
