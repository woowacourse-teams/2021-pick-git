import axios from "axios";
import { useContext, useEffect, useState } from "react";

import { HTTPErrorHandler, SearchResultUser } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import SearchContext from "../../contexts/SearchContext";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { removeDuplicatedData } from "../../utils/data";
import { getAPIErrorMessage, handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import { useSearchUserResultQuery } from "../queries/search";

const useSearchUserData = () => {
  const { keyword } = useContext(SearchContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);

  const [results, setResults] = useState<SearchResultUser[]>([]);
  const [isAllResultFetched, setIsAllResultFetched] = useState(false);
  const { data, error, isError, isLoading, fetchNextPage, isFetchingNextPage, refetch } =
    useSearchUserResultQuery(keyword);

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
    const filteredData = removeDuplicatedData<SearchResultUser>(fetchedData, (data) => data);

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
    handleError();
  }, [error]);

  useEffect(() => {
    handleDataFetch();
  }, [data]);

  return { results, isError, isLoading, isFetchingNextPage, handleIntersect, refetch };
};

export default useSearchUserData;
