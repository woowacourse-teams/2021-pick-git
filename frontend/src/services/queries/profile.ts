import { QueryFunction, useMutation, useQuery, useQueryClient } from "react-query";
import axios, { AxiosError } from "axios";

import { ProfileData } from "../../@types";
import { QUERY } from "../../constants/queries";
import { requestAddFollow, requestDeleteFollow, requestGetSelfProfile, requestGetUserProfile } from "../requests";
import UserContext from "../../contexts/UserContext";
import { useContext } from "react";
import { getAccessToken } from "../../storage/storage";
import SnackBarContext from "../../contexts/SnackbarContext";

type ProfileQueryKey = readonly [
  typeof QUERY.GET_PROFILE,
  {
    isMyProfile: boolean;
    username: string | null;
  }
];

export const useProfileQuery = (isMyProfile: boolean, username: string | null) => {
  const profileQueryFunction: QueryFunction<ProfileData> = async ({ queryKey }) => {
    const [, { isMyProfile, username }] = queryKey as ProfileQueryKey;
    const accessToken = getAccessToken();

    if (isMyProfile) {
      if (!accessToken) throw Error("no accessToken");

      return await requestGetSelfProfile(accessToken);
    } else {
      if (!username) throw Error("no username");

      return await requestGetUserProfile(username, accessToken);
    }
  };

  return useQuery<ProfileData, AxiosError<ProfileData>>(
    [QUERY.GET_PROFILE, { isMyProfile, username }],
    profileQueryFunction
  );
};

const useFollowMutation = (
  username: string | undefined,
  callback: (userName: string | undefined, accessToken: string | null) => Promise<any>
) => {
  const queryClient = useQueryClient();
  const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, username }];
  const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);
  const { logout } = useContext(UserContext);
  const { pushMessage } = useContext(SnackBarContext);

  return useMutation(() => callback(username, getAccessToken()), {
    onSuccess: ({ followerCount, following }) => {
      if (!currentProfileQueryData) return;

      queryClient.setQueryData<ProfileData>(currentProfileQueryKey, {
        ...currentProfileQueryData,
        followerCount,
        following,
      });
    },

    onError: (error) => {
      if (axios.isAxiosError(error)) {
        const { status } = error.response ?? {};

        if (status === 401) {
          pushMessage("로그인한 사용자만 이용할 수 있는 기능입니다.");
          logout();
        }

        return;
      }
      pushMessage("요청하신 작업을 수행할 수 없습니다.");
    },
  });
};

export const useFollowingMutation = (userName: string | undefined) => useFollowMutation(userName, requestAddFollow);

export const useUnfollowingMutation = (userName: string | undefined) =>
  useFollowMutation(userName, requestDeleteFollow);
