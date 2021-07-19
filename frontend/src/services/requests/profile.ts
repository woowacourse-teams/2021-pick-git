import axios from "axios";

import { ProfileData } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetSelfProfile = async (accessToken: string | null) => {
  if (!accessToken) {
    throw Error("no accessToken");
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

export const requestAddFollow = async (username: string | undefined, accessToken: string | null) => {
  if (!accessToken || !username) {
    throw Error("Invalid Request");
  }

  const response = await axios.post(API_URL.USER_PROFILE_FOLLOW(username), null, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestDeleteFollow = async (username: string | undefined, accessToken: string | null) => {
  if (!accessToken || !username) {
    throw Error("Invalid Request");
  }

  const response = await axios.delete(API_URL.USER_PROFILE_FOLLOW(username), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};
