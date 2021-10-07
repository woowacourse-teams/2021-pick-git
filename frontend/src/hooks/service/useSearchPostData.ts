import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { InfiniteData, useQueryClient } from "react-query";
import { HTTPErrorHandler, Post } from "../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../constants/messages";
import { QUERY } from "../constants/queries";
import SearchContext from "../contexts/SearchContext";
import SnackBarContext from "../contexts/SnackbarContext";
import UserContext from "../contexts/UserContext";
import { getAPIErrorMessage, handleHTTPError } from "../utils/error";
import { isHttpErrorStatus } from "../utils/typeGuard";
import { useSearchPostResultQuery } from "../services/queries/search";

const useSearchPostData = (type: string | null, prevData?: InfiniteData<Post[]> | null, activated?: boolean) => {
  const [prevKeyword, setPrevKeyword] = useState<string | null>(null);
  const { keyword } = useContext(SearchContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);
  const queryClient = useQueryClient();
  const formattedKeyword = keyword.trim().replace(/,/g, " ").replace(/\s+/g, " ");
  const queryKey = [QUERY.GET_SEARCH_POST_RESULT, { type, formattedKeyword }];

  const [isAllResultFetched, setIsAllResultFetched] = useState(false);
  const {
    data: infinitePostsData,
    error,
    isError,
    isLoading,
    fetchNextPage,
    isFetchingNextPage,
    refetch,
  } = useSearchPostResultQuery(type, formattedKeyword, queryKey, activated);
  const handleIntersect = async () => {
    if (isAllResultFetched) return;

    await fetchNextPage();
  };

  const handleDataFetch = () => {
    if (!infinitePostsData) {
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
    if (prevData) {
      queryClient.setQueryData([QUERY.GET_SEARCH_POST_RESULT, { type, keyword }], prevData);
    }
  }, []);

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
    queryKey,
    formattedKeyword,
  };
};

export default useSearchPostData;
