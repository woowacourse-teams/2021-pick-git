import axios, { AxiosError } from "axios";
import { useContext } from "react";
import { useQueryClient } from "react-query";

import { ProfileData } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import { QUERY } from "../../constants/queries";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError } from "../../utils/api";
import { useFollowingMutation, useUnfollowingMutation } from "../queries";

const useFollow = () => {
  const { logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const queryClient = useQueryClient();
  const { mutateAsync: mutateToFollow, isLoading: isFollowLoading } = useFollowingMutation();
  const { mutateAsync: mutateToUnFollow, isLoading: isUnfollowLoading } = useUnfollowingMutation();

  const setProfileQueryData = (followerCount: number, following: boolean) => {
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

      if (status) {
        handleHTTPError(status, {
          unauthorized: () => logout(),
        });
      }

      pushSnackbarMessage(data.errorCode);
    } else {
      pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
    }
  };

  const toggleFollow = async (username: string, isFollowing: boolean) => {
    try {
      const { followerCount, following } = isFollowing
        ? await mutateToUnFollow(username)
        : await mutateToFollow(username);

      setProfileQueryData(followerCount, following);
    } catch (error) {
      handleError(error);
    }
  };

  return { toggleFollow, isFollowLoading, isUnfollowLoading };
};

export default useFollow;
