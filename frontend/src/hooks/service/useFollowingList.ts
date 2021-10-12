import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { HTTPErrorHandler, UserItem } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { removeDuplicatedData } from "../../utils/data";
import { getAPIErrorMessage, handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import { useFollowingsQuery } from "../../services/queries";

const useFollowingList = (username: string | null) => {
  const [results, setResults] = useState<UserItem[]>([]);
  const [isAllResultFetched, setIsAllResultFetched] = useState(false);
  const { data, error, isLoading, isError, isFetchingNextPage, refetch, fetchNextPage } = useFollowingsQuery(username);

  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);

  const handleIntersect = async () => {
    if (isAllResultFetched) return;

    await fetchNextPage();
  };

  const handleDataFetch = () => {
    if (!data) {
      return;
    }

    const { pages } = data ?? {};
    const lastPage = pages?.[pages.length - 1];

    if (!lastPage || !lastPage.length) {
      setIsAllResultFetched(true);
    }

    const fetchedData = pages.map((page) => page ?? []).reduce((acc, page) => [...acc, ...page], []);
    const filteredData = removeDuplicatedData<UserItem>(fetchedData, (data) => data);

    setResults(filteredData);
  };

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status, data } = error.response ?? {};
      const errorHandler: HTTPErrorHandler = {
        unauthorized: () => {
          logout();
          refetch();
        },
      };

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, errorHandler);
      }

      data?.errorCode && pushSnackbarMessage(getAPIErrorMessage(data.errorCode));
    } else {
      pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
    }
  };

  useEffect(() => {
    handleDataFetch();
  }, [data]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { results, isError, isLoading, isFetchingNextPage, handleIntersect, refetch };
};

export default useFollowingList;
