import axios from "axios";

import { MutateResponseFollow, ProfileData } from "../../@types";
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

export const requestAddFollow = async (username: string, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.post<MutateResponseFollow>(API_URL.USER_PROFILE_FOLLOW(username), null, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
};

export const requestDeleteFollow = async (username: string, accessToken: string | null) => {
  if (!accessToken) {
    throw customError.noAccessToken;
  }

  const response = await axios.delete<MutateResponseFollow>(API_URL.USER_PROFILE_FOLLOW(username), {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

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
