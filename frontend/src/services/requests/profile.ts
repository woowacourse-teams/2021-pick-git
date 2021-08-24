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

export const requestSetProfileImage = async (profileImage: File, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const fileReader = new FileReader();
  const waitFileReaderLoad = new Promise<string>((resolve, reject) => {
    fileReader.onload = () => {
      if (fileReader.error) {
        reject(fileReader.error);
      }

      resolve("done");
    };
  });

  fileReader.readAsArrayBuffer(profileImage);
  try {
    await waitFileReaderLoad;
  } catch (error) {
    console.error(error);

    throw customError.fileReader;
  }

  if (fileReader.result && fileReader.result instanceof ArrayBuffer) {
    const imageByteArray = new Int8Array(fileReader.result);

    const response = await axios.put<{ imageUrl: string }>(API_URL.SELF_PROFILE_IMAGE, imageByteArray, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        "Content-Type": "application/octet-stream",
      },
    });

    return response.data;
  }

  return null;
};

export const requestSetProfileDescription = async (description: string, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.put<{ description: string }>(
    API_URL.SELF_PROFILE_DESCRIPTION,
    { description },
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        "Content-Type": "application/json",
      },
    }
  );

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
