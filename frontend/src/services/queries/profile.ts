import { QueryFunction, useMutation, useQuery, useQueryClient } from "react-query";
import axios, { AxiosError } from "axios";

import { Profile } from "../../@types";
import { QUERY } from "../../constants/queries";
import useLocalStorage from "../hooks/@common/useLocalStorage";
import { requestAddFollow, requestDeleteFollow, requestGetSelfProfile, requestGetUserProfile } from "../requests";
import UserContext from "../../contexts/UserContext";
import { useContext } from "react";

type ProfileQueryKey = readonly [
  typeof QUERY.GET_PROFILE,
  {
    isMyProfile: boolean;
    accessToken: string | null;
    userName: string | null;
  }
];

const profileQueryFunction: QueryFunction<Profile> = async ({ queryKey }) => {
  const [, { isMyProfile, accessToken, userName }] = queryKey as ProfileQueryKey;

  if (isMyProfile) {
    if (!accessToken) throw Error("no accessToken");

    return await requestGetSelfProfile(accessToken);
  } else {
    return await requestGetUserProfile(userName as string, accessToken);
  }
};

export const useProfileQuery = (isMyProfile: boolean, userName: string | null) => {
  const { accessToken } = useLocalStorage();

  return useQuery<Profile, AxiosError<Profile>>(
    [QUERY.GET_PROFILE, { isMyProfile, accessToken, userName }],
    profileQueryFunction
  );
};

const useFollowMutation = (
  userName: string | undefined,
  callback: (userName: string, accessToken: string) => Promise<any>
) => {
  const { accessToken } = useLocalStorage();
  const queryClient = useQueryClient();
  const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, accessToken, userName }];
  const currentProfileQueryData = queryClient.getQueryData<Profile>(currentProfileQueryKey);
  const { logout } = useContext(UserContext);

  if (!accessToken || !userName) {
    throw Error("Invalid Request");
  }

  return useMutation(() => callback(userName, accessToken), {
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

export const useFollowingMutation = (userName: string | undefined) => useFollowMutation(userName, requestAddFollow);

export const useUnfollowingMutation = (userName: string | undefined) =>
  useFollowMutation(userName, requestDeleteFollow);
