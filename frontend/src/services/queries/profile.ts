import { QueryFunction, useInfiniteQuery, useMutation, useQuery } from "react-query";
import { AxiosError } from "axios";

import { ErrorResponse, MutateResponseFollow, ProfileData, UserItem } from "../../@types";
import { QUERY } from "../../constants/queries";
import {
  requestAddFollow,
  requestDeleteFollow,
  requestGetFollowers,
  requestGetFollowings,
  requestGetSelfProfile,
  requestGetUserProfile,
  requestSetProfile,
} from "../requests";
import { getAccessToken } from "../../storage/storage";
import { customError } from "../../utils/error";

type ProfileQueryKey = readonly [
  typeof QUERY.GET_PROFILE,
  {
    isMyProfile: boolean;
    username: string | null;
  }
];

interface SetProfileVariable {
  image: File | null;
  description: string;
}

interface SetProfileResponse {
  imageUrl: string;
  description: string;
}

interface MutateFollowVariable {
  username: string;
  applyGithub: boolean;
}

export const useProfileQuery = (isMyProfile: boolean, username: string | null) => {
  const profileQueryFunction: QueryFunction<ProfileData> = async ({ queryKey }) => {
    const [, { isMyProfile, username }] = queryKey as ProfileQueryKey;
    const accessToken = getAccessToken();

    if (isMyProfile) {
      if (!accessToken) throw customError.noAccessToken;

      return await requestGetSelfProfile(accessToken);
    } else {
      if (!username) throw Error("no username");

      return await requestGetUserProfile(username, accessToken);
    }
  };

  return useQuery<ProfileData, AxiosError<ErrorResponse>>(
    [QUERY.GET_PROFILE, { isMyProfile, username }],
    profileQueryFunction
  );
};

export const useFollowingMutation = () =>
  useMutation<MutateResponseFollow, AxiosError<ErrorResponse>, MutateFollowVariable>(({ username, applyGithub }) =>
    requestAddFollow(username, applyGithub, getAccessToken())
  );

export const useUnfollowingMutation = () =>
  useMutation<MutateResponseFollow, AxiosError<ErrorResponse>, MutateFollowVariable>(({ username, applyGithub }) =>
    requestDeleteFollow(username, applyGithub, getAccessToken())
  );

export const useProfileMutation = () => {
  return useMutation<SetProfileResponse, AxiosError<ErrorResponse> | Error, SetProfileVariable>(
    ({ image, description }) => requestSetProfile(image, description, getAccessToken())
  );
};

export const useFollowingsQuery = (username: string | null) => {
  return useInfiniteQuery<UserItem[] | null, AxiosError<ErrorResponse>>(
    [QUERY.GET_PROFILE_FOLLOWING, { username }],
    async ({ pageParam = 0 }) => await requestGetFollowings(username, pageParam, getAccessToken()),
    { getNextPageParam: (_, pages) => pages.length }
  );
};

export const useFollowersQuery = (username: string | null) => {
  return useInfiniteQuery<UserItem[] | null, AxiosError<ErrorResponse>>(
    [QUERY.GET_PROFILE_FOLLOWER, { username }],
    async ({ pageParam = 0 }) => await requestGetFollowers(username, pageParam, getAccessToken()),
    { getNextPageParam: (_, pages) => pages.length }
  );
};
