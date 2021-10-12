import axios, { AxiosError } from "axios";
import { useContext } from "react";

import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { getAPIErrorMessage, getClientErrorMessage, handleClientError, handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus, isClientErrorCode } from "../../utils/typeGuard";
import { useFollowingMutation, useUnfollowingMutation } from "../../services/queries";

const useFollow = (querySetter: (following: boolean) => void) => {
  const { logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const { mutateAsync: mutateToFollow, isLoading: isFollowingMutationLoading } = useFollowingMutation();
  const { mutateAsync: mutateToUnFollow, isLoading: isFollowerMutationLoading } = useUnfollowingMutation();

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
      querySetter(!isFollowing);

      const { following } = isFollowing
        ? await mutateToUnFollow({ username, applyGithub })
        : await mutateToFollow({ username, applyGithub });

      if (following === isFollowing) {
        pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
        querySetter(isFollowing);
      }
    } catch (error) {
      handleError(error);
      querySetter(isFollowing);
    }
  };

  return { toggleFollow, isLoading: isFollowerMutationLoading || isFollowingMutationLoading };
};

export default useFollow;
