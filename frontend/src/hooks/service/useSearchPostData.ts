import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { HTTPErrorHandler } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { getAPIErrorMessage, handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";
import SearchPostContext from "../../contexts/SearchPostContext";

interface Params {
  keyword: string;
  type: string;
  activated: boolean;
}

const useSearchPostData = ({ keyword, type, activated }: Params) => {
  const [prevKeyword, setPrevKeyword] = useState<string | null>(null);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);
  const { queryResult, initSearchPost, initialized } = useContext(SearchPostContext);

  const [isAllResultFetched, setIsAllResultFetched] = useState(false);
  const {
    data: infinitePostsData,
    error,
    isError,
    isLoading,
    fetchNextPage,
    isFetchingNextPage,
    refetch,
  } = queryResult;

  const handleIntersect = async () => {
    if (isAllResultFetched) return;

    await fetchNextPage();
  };

  const handleDataFetch = () => {
    if (!infinitePostsData || !initialized) {
      return;
    }

    const { pages } = infinitePostsData;
    const [firstPage] = pages;

    if (!firstPage) {
      return;
    }

    const lastPage = pages[pages.length - 1];

    if (!lastPage || !lastPage.length) {
      setIsAllResultFetched(true);
    }
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
    initSearchPost(type, keyword, activated);
  }, [type, keyword, activated]);

  useEffect(() => {
    if (prevKeyword !== keyword) {
      setPrevKeyword(keyword);
      setIsAllResultFetched(false);
    }
  }, [keyword]);

  useEffect(() => {
    handleError();
  }, [error]);

  useEffect(() => {
    handleDataFetch();
  }, [infinitePostsData]);

  return {
    infinitePostsData,
    isError,
    isLoading,
    isFetchingNextPage,
    handleIntersect,
    refetch,
  };
};

export default useSearchPostData;
