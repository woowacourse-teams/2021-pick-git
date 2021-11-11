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
  requestSetProfileDescription,
  requestSetProfileImage,
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
    profileQueryFunction,
    { suspense: true, cacheTime: 3600 * 4 }
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

export const useProfileImageMutation = () => {
  return useMutation<{ imageUrl: string } | null, AxiosError<ErrorResponse> | Error, { image: File }>(({ image }) =>
    requestSetProfileImage(image, getAccessToken())
  );
};

export const useProfileDescriptionMutation = () => {
  return useMutation<{ description: string }, AxiosError<ErrorResponse> | Error, { description: string }>(
    ({ description }) => requestSetProfileDescription(description, getAccessToken())
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
