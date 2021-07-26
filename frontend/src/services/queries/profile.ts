import { QueryFunction, useMutation, useQuery, useQueryClient } from "react-query";
import axios, { AxiosError } from "axios";

import { ErrorResponse, ProfileData } from "../../@types";
import { QUERY } from "../../constants/queries";
import {
  requestAddFollow,
  requestDeleteFollow,
  requestGetSelfProfile,
  requestGetUserProfile,
  requestSetProfile,
} from "../requests";
import UserContext from "../../contexts/UserContext";
import { useContext } from "react";
import { getAccessToken } from "../../storage/storage";
import SnackBarContext from "../../contexts/SnackbarContext";
import { SUCCESS_MESSAGE, UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import { handleHTTPError } from "../../utils/api";

type ProfileQueryKey = readonly [
  typeof QUERY.GET_PROFILE,
  {
    isMyProfile: boolean;
    username: string | null;
  }
];

interface SetProfileVariable {
  image: File | null;
  description: string | null;
}

interface SetProfileResponse {
  imageUrl: string;
  description: string;
}

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

  return useQuery<ProfileData, AxiosError<ErrorResponse>>(
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
  const { pushSnackbarMessage } = useContext(SnackBarContext);

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
        const { status, data } = error.response ?? {};

        if (status) {
          handleHTTPError(status, {
            unauthorized: () => logout(),
          });
        }

        pushSnackbarMessage(data.errorCode);
      } else {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      }
    },
  });
};

export const useFollowingMutation = (username: string | undefined) => useFollowMutation(username, requestAddFollow);

export const useUnfollowingMutation = (username: string | undefined) =>
  useFollowMutation(username, requestDeleteFollow);

export const useProfileMutation = (username: string | null) => {
  const queryClient = useQueryClient();
  const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, username }];
  const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);
  const { logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  return useMutation<SetProfileResponse, AxiosError<ErrorResponse> | Error, SetProfileVariable>(
    ({ image, description }) => requestSetProfile(image, description, getAccessToken()),
    {
      onSuccess: ({ imageUrl, description }) => {
        if (!currentProfileQueryData) return;

        queryClient.setQueryData<ProfileData>(currentProfileQueryKey, {
          ...currentProfileQueryData,
          imageUrl,
          description,
        });

        pushSnackbarMessage(SUCCESS_MESSAGE.SET_PROFILE);
      },
      onError: (error) => {
        if (axios.isAxiosError(error)) {
          const { status, data } = error.response ?? {};

          if (status) {
            handleHTTPError(status, {
              unauthorized: () => logout(),
              notFound: () => pushSnackbarMessage("아직 준비되지 않은 서비스입니다."),
              methodNotAllowed: () => pushSnackbarMessage("아직 준비되지 않은 서비스입니다."),
            });
          }

          data?.errorCode && pushSnackbarMessage(data.errorCode);
        } else {
          pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
        }
      },
    }
  );
};
