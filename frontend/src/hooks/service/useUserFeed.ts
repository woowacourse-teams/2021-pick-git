import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import { PAGE_URL } from "../../constants/urls";

import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import UserFeedContext from "../../contexts/UserFeedContext";

import { handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";

import type { HTTPErrorHandler } from "../../@types";

const useUserFeed = (isMyFeed: boolean, username: string | null) => {
  const [isAllPostsFetched, setIsAllPostsFetched] = useState(false);
  const history = useHistory();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);
  const { queryResult, initialized, initUserFeed } = useContext(UserFeedContext);

  const {
    data: infinitePostsData,
    isLoading,
    error,
    isError,
    refetch,
    fetchNextPage,
    isFetchingNextPage,
  } = queryResult;

  const handleIntersect = () => {
    if (isAllPostsFetched) return;

    fetchNextPage();
  };

  const handleDataFetch = () => {
    if (!infinitePostsData || !initialized) return;

    const pages = infinitePostsData.pages;

    if (!pages) return;

    const lastPage = pages[pages.length - 1];

    if (!lastPage || !lastPage.length) {
      setIsAllPostsFetched(true);
    }
  };

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status, data } = error.response ?? {};
      const errorHandler: HTTPErrorHandler = {
        unauthorized: () => {
          if (isMyFeed) {
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

      data?.errorCode && pushSnackbarMessage(data.errorCode);
    }
  };

  useEffect(() => {
    initUserFeed(isMyFeed, username);
  }, [isMyFeed, username]);

  useEffect(() => {
    handleDataFetch();
  }, [infinitePostsData]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { infinitePostsData, handleIntersect, isLoading, isError, isFetchingNextPage, refetch };
};

export default useUserFeed;
