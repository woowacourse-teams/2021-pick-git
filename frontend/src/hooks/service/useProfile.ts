import axios from "axios";
import { useContext, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { HTTPErrorHandler } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";

import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { getAPIErrorMessage, handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import { useProfileQuery } from "../../services/queries";

const useProfile = (isMyProfile: boolean, username: string | null) => {
  const history = useHistory();
  const { isLoggedIn, logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { isLoading, error, data, refetch } = useProfileQuery(isMyProfile, username);

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status, data } = error.response ?? {};
      const errorHandler: HTTPErrorHandler = {
        unauthorized: () => {
          if (isMyProfile) {
            history.push(PAGE_URL.HOME);
          } else {
            logout();
            refetch();
          }
        },
      };

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, errorHandler);
      }

      pushSnackbarMessage(data ? getAPIErrorMessage(data.errorCode) : UNKNOWN_ERROR_MESSAGE);
    } else {
      pushSnackbarMessage("프로필을 확인할 수 없습니다.");
      history.push(PAGE_URL.HOME);
    }
  };

  useEffect(() => {
    handleError();
  }, [error]);

  return { data, isLoading };
};

export default useProfile;
