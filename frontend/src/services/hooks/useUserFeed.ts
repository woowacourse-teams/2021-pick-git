import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { InfiniteData, useQueryClient } from "react-query";
import { useHistory } from "react-router-dom";

import { HTTPErrorHandler, Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import { PAGE_URL } from "../../constants/urls";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import { useUserPostsQuery } from "../queries";

const useUserFeed = (isMyFeed: boolean, username: string | null, prevData?: InfiniteData<Post[]>) => {
  const [isAllPostsFetched, setIsAllPostsFetched] = useState(false);
  const {
    data: infinitePostsData,
    isLoading,
    error,
    isError,
    refetch,
    fetchNextPage,
    isFetchingNextPage,
  } = useUserPostsQuery(isMyFeed, username);

  const queryClient = useQueryClient();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);
  const history = useHistory();

  const handleIntersect = () => {
    if (isAllPostsFetched) return;

    fetchNextPage();
  };

  const handleDataFetch = () => {
    if (!infinitePostsData) return;

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
    if (prevData) {
      queryClient.setQueryData([QUERY.GET_USER_FEED_POSTS, { isMyFeed, username }], prevData);
    }
  }, []);

  useEffect(() => {
    handleDataFetch();
  }, [infinitePostsData]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { infinitePostsData, handleIntersect, isLoading, isError, isFetchingNextPage, refetch };
};

export default useUserFeed;
