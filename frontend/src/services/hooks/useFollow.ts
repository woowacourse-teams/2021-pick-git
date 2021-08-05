import axios, { AxiosError } from "axios";
import { useContext } from "react";
import { useQueryClient } from "react-query";

import { ProfileData } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import { QUERY } from "../../constants/queries";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { getAPIErrorMessage, getClientErrorMessage, handleClientError, handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus, isClientErrorCode } from "../../utils/typeGuard";
import { useFollowingMutation, useUnfollowingMutation } from "../queries";

const useFollow = () => {
  const { logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const queryClient = useQueryClient();
  const { mutateAsync: mutateToFollow, isLoading: isFollowLoading } = useFollowingMutation();
  const { mutateAsync: mutateToUnFollow, isLoading: isUnfollowLoading } = useUnfollowingMutation();

  const setProfileQueryData = (username: string, followerCount: number, following: boolean) => {
    const currentProfileQueryKey = [QUERY.GET_PROFILE, { isMyProfile: false, username }];
    const currentProfileQueryData = queryClient.getQueryData<ProfileData>(currentProfileQueryKey);

    if (!currentProfileQueryData) return;

    queryClient.setQueryData<ProfileData>(currentProfileQueryKey, {
      ...currentProfileQueryData,
      followerCount,
      following,
    });
  };

  const handleError = (error: AxiosError | Error) => {
    if (axios.isAxiosError(error)) {
      const { status, data } = error.response ?? {};

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, {
          unauthorized: () => logout(),
        });
      }

      pushSnackbarMessage(data ? getAPIErrorMessage(data.errorCode) : UNKNOWN_ERROR_MESSAGE);
    } else {
      const { message } = error;

      if (isClientErrorCode(message)) {
        handleClientError(message, {
          noAccessToken: () => {
            pushSnackbarMessage(getClientErrorMessage(message));
            logout();
          },
        });
      } else {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
      }
    }
  };

  const toggleFollow = async (username: string, isFollowing: boolean, applyGithub: boolean) => {
    try {
      const { followerCount, following } = isFollowing
        ? await mutateToUnFollow({ username, applyGithub })
        : await mutateToFollow({ username, applyGithub });

      setProfileQueryData(username, followerCount, following);
    } catch (error) {
      handleError(error);
    }
  };

  return { toggleFollow, isFollowLoading, isUnfollowLoading };
};

export default useFollow;
