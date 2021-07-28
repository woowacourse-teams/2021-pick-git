import axios from "axios";
import { useContext, useEffect, useState } from "react";

import { SearchResult, SearchResultUser } from "../../@types";
import { UNKNOWN_ERROR_MESSAGE } from "../../constants/messages";
import SearchContext from "../../contexts/SearchContext";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError, HTTPErrorHandler } from "../../utils/api";
import { removeDuplicatedData } from "../../utils/data";
import { useSearchResultQuery } from "../queries/search";

const useSearchData = () => {
  const { keyword, onKeywordChange } = useContext(SearchContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);

  const [results, setResults] = useState<SearchResult>({ users: [], tags: [] });
  const [currentPageIndex, setCurrentPageIndex] = useState<{ [k in keyof SearchResult]: number }>({
    users: 0,
    tags: 0,
  });
  const [isAllResultFetched, setIsAllResultFetched] = useState(false);
  const { data, error, isError, isLoading, fetchNextPage, isFetchingNextPage, refetch } = useSearchResultQuery(keyword);

  const updateNewPage = (key: keyof SearchResult, pages: (SearchResult | null)[], pageIndex: number) => {
    const fetchedData = pages
      .slice(0, pageIndex + 1)
      .map((page) => page?.[key] ?? [])
      .reduce((acc, data) => [...acc, ...data], []);
    const filteredData = removeDuplicatedData<SearchResult[typeof key][number]>(fetchedData, (data) => data);

    setResults((prevResult) => ({
      ...prevResult,
      [key]: filteredData,
    }));
    setCurrentPageIndex((prevPageIndex) => ({
      ...prevPageIndex,
      [key]: pageIndex,
    }));
  };

  const handleIntersect = async (key: keyof SearchResult) => {
    if (!data || !data.pages[currentPageIndex[key]]) {
      if (isAllResultFetched) return;

      const { pages } = data ?? {};
      const lastPage = pages?.[pages.length - 1];

      if (!lastPage?.[key] || !lastPage[key].length) {
        setIsAllResultFetched(true);
      }

      await fetchNextPage();
    }

    updateNewPage(key, data?.pages ?? [], currentPageIndex[key]);
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

      if (status) {
        handleHTTPError(status, errorHandler);
      }

      data?.errorCode && pushSnackbarMessage(data.errorCode);
    }

    pushSnackbarMessage(UNKNOWN_ERROR_MESSAGE);
  };

  useEffect(() => {
    handleError();
  }, [error]);

  useEffect(() => {
    handleIntersect("users");
  }, [data]);

  useEffect(() => {
    onKeywordChange("");
  }, []);

  return { results, isError, isLoading, isFetchingNextPage, handleIntersect, refetch };
};

export default useSearchData;
