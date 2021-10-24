import axios from "axios";
import { useContext, useEffect } from "react";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { getAPIErrorMessage, handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import { useGithubStatsQuery } from "../../services/queries";

const useGithubStatistics = (username: string, activated: boolean) => {
  const { data, error, isLoading, isError, isFetching, refetch } = useGithubStatsQuery(username, activated);
  const { logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const handleError = () => {
    if (!error) {
      return;
    }

    if (axios.isAxiosError(error)) {
      const { status, data } = error.response ?? {};

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, {
          unauthorized: () => logout(),
        });
      }

      pushSnackbarMessage(data ? getAPIErrorMessage(data.errorCode) : UNKNOWN_ERROR_MESSAGE);
    } else {
      pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
    }
  };

  useEffect(() => {
    handleError();
  }, [error]);

  return { data, isLoading, isError, isFetching, refetch };
};

export default useGithubStatistics;
