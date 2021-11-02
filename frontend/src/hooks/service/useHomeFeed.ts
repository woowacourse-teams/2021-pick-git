import axios from "axios";
import { useContext, useEffect } from "react";
import { useQueryClient } from "react-query";

import { QUERY } from "../../constants/queries";

import { removeDuplicatedData } from "../../utils/data";
import { handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";

import useFeedMutation from "./useFeedMutation";

import { FeedFilterOption, Post } from "../../@types";
import HomeFeedContext from "../../contexts/HomeFeedContext";
import useAuth from "../common/useAuth";

const tmp: { current: FeedFilterOption | null } = { current: null };
const queryKeyList = [QUERY.GET_HOME_FEED_POSTS("followings"), QUERY.GET_HOME_FEED_POSTS("all")];

const useHomeFeed = () => {
  const {
    queryResults,
    feedFilterOption,
    currentPostId,
    initialized,
    setFeedFilterOption,
    setCurrentPostId,
    refetchAll,
  } = useContext(HomeFeedContext);

  const {
    data: infinitePostsData,
    isLoading,
    error,
    isError,
    isFetching,
    fetchNextPage,
    refetch,
  } = queryResults[feedFilterOption];

  const { setPostsPages } = useFeedMutation(queryKeyList);
  const { isLoggedIn, logout } = useAuth();
  const queryClient = useQueryClient();

  tmp.current = feedFilterOption;

  const handlePostsEndIntersect = () => {
    queryResults[tmp.current ?? "all"].fetchNextPage();
  };

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, {
          unauthorized: () => {
            logout();
            queryClient.refetchQueries(QUERY.GET_HOME_FEED_POSTS(feedFilterOption), { active: true });
          },
        });
      }
    }
  };

  const handleDataFetch = () => {
    if (!infinitePostsData || !initialized) {
      return;
    }

    const filteredPages = infinitePostsData.pages.map((page) => removeDuplicatedData<Post>(page, (page) => page.id));

    queryKeyList.forEach((queryKey) => setPostsPages(filteredPages, queryKey));
  };

  useEffect(() => {
    handleDataFetch();
  }, [infinitePostsData]);

  useEffect(() => {
    handleError();
  }, [error]);

  return {
    infinitePostsData,
    isLoading,
    isFetching,
    isError,
    handlePostsEndIntersect,
    refetch,
    feedFilterOption,
    currentPostId,
    setFeedFilterOption,
    setCurrentPostId,
    refetchAll,
  };
};

export default useHomeFeed;
