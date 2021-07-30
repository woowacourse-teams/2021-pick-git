import axios from "axios";
import { useContext, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { HTTPErrorHandler } from "../../@types";
import { PAGE_URL } from "../../constants/urls";

import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import { useProfileQuery } from "../queries";

const useProfile = (isMyProfile: boolean, username: string | null) => {
  const history = useHistory();
  const { isLoggedIn, logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { isLoading, error, data, refetch } = useProfileQuery(isMyProfile, username);

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};
      const errorHandler: HTTPErrorHandler = {
        unauthorized: () => {
          if (isMyProfile) {
            pushSnackbarMessage("로그인한 사용자만 사용할 수 있는 서비스입니다.");

            history.push(PAGE_URL.HOME);
          } else {
            isLoggedIn && pushSnackbarMessage("사용자 정보가 유효하지 않아 자동으로 로그아웃합니다.");
            logout();
            refetch();
          }
        },
      };

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, errorHandler);
      }
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
