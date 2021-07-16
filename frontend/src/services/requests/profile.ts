import axios from "axios";

import { Profile } from "../../@types";
import { API_URL } from "../../constants/urls";

export const requestGetSelfProfile = async (accessToken: string) => {
  const response = await axios.get<Profile>(API_URL.SELF_PROFILE, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestGetUserProfile = async (userName: string, accessToken: string | null) => {
  const config = accessToken
    ? {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    : {};
  const response = await axios.get<Profile>(API_URL.USER_PROFILE(userName), config);

  return response.data;
};

export const requestAddFollow = async (userName: string, accessToken: string) => {
  const response = await axios.post(API_URL.USER_PROFILE_FOLLOW(userName), null, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestDeleteFollow = async (userName: string, accessToken: string) => {
  const response = await axios.delete(API_URL.USER_PROFILE_FOLLOW(userName), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};
