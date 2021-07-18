import { QueryFunction, useMutation, useQuery, useQueryClient } from "react-query";
import axios, { AxiosError } from "axios";

import { Profile } from "../../@types";
import { QUERY } from "../../constants/queries";
import storage from "../../storage/storage";
import { requestAddFollow, requestDeleteFollow, requestGetSelfProfile, requestGetUserProfile } from "../requests";
import UserContext from "../../contexts/UserContext";
import { useContext } from "react";

type ProfileQueryKey = readonly [
  typeof QUERY.GET_PROFILE,
  {
    isMyProfile: boolean;
    username: string | null;
  }
];

export const useProfileQuery = (isMyProfile: boolean, username: string | null) => {
  const { getAccessToken } = storage();

  const profileQueryFunction: QueryFunction<Profile> = async ({ queryKey }) => {
    const [, { isMyProfile, username }] = queryKey as ProfileQueryKey;
    const accessToken = getAccessToken();

    if (isMyProfile) {
      if (!accessToken) throw Error("no accessToken");

      return await requestGetSelfProfile(accessToken);
    } else {
      return await requestGetUserProfile(username as string, accessToken);
    }
  };

  return useQuery<Profile, AxiosError<Profile>>([QUERY.GET_PROFILE, { isMyProfile, username }], profileQueryFunction);
};

const useFollowMutation = (
  username: string | undefined,
  callback: (username: string | undefined, accessToken: string | null) => Promise<any>
) => {
  const { getAccessToken } = storage();
  const queryClient = useQueryClient();
  const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, accessToken: getAccessToken(), username }];
  const currentProfileQueryData = queryClient.getQueryData<Profile>(currentProfileQueryKey);
  const { logout } = useContext(UserContext);

  return useMutation(() => callback(username, getAccessToken()), {
    onSuccess: ({ followerCount, following }) => {
      if (!currentProfileQueryData) return;

      queryClient.setQueryData<Profile>(currentProfileQueryKey, {
        ...currentProfileQueryData,
        followerCount,
        following,
      });
    },

    onError: (error) => {
      if (axios.isAxiosError(error)) {
        const { status } = error.response ?? {};

        if (status === 401) {
          alert("로그인한 사용자만 이용할 수 있는 기능입니다.");
          logout();
        }

        return;
      }
      alert("요청하신 작업을 수행할 수 없습니다.");
    },
  });
};

export const useFollowingMutation = (username: string | undefined) => useFollowMutation(username, requestAddFollow);

export const useUnfollowingMutation = (username: string | undefined) =>
  useFollowMutation(username, requestDeleteFollow);
