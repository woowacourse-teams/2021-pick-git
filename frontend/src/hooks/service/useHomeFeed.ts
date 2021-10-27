import axios from "axios";
import { useContext, useEffect } from "react";
import { useQueryClient } from "react-query";

import { QUERY } from "../../constants/queries";

import UserContext from "../../contexts/UserContext";

import { useHomeFeedPostsQuery } from "../../services/queries";

import { removeDuplicatedData } from "../../utils/data";
import { handleHTTPError } from "../../utils/error";
import { isHttpErrorStatus } from "../../utils/typeGuard";

import useFeedMutation from "./useFeedMutation";

import type { Post } from "../../@types";
import HomeFeedContext from "../../contexts/HomeFeedContext";
import useAuth from "../common/useAuth";

const useHomeFeed = () => {
  const {
    queryResult,
    feedFilterOption,
    currentPostId,
    initialized,
    initHomeFeed,
    setFeedFilterOption,
    setCurrentPostId,
  } = useContext(HomeFeedContext);

  const { data: infinitePostsData, isLoading, error, isError, isFetching, fetchNextPage, refetch } = queryResult;

  // TODO : 그냥 QUERY 만 보내도 되는지 알아보기
  const { setPostsPages } = useFeedMutation([QUERY]);
  const { isLoggedIn, logout } = useAuth();
  const queryClient = useQueryClient();

  const handlePostsEndIntersect = () => {
    fetchNextPage();
  };

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      if (status && isHttpErrorStatus(status)) {
        handleHTTPError(status, {
          unauthorized: () => {
            logout();
            queryClient.refetchQueries(QUERY.GET_HOME_FEED_POSTS, { active: true });
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

    setPostsPages(filteredPages);
  };

  useEffect(() => {
    if (!initialized) {
      initHomeFeed(isLoggedIn ? "followings" : "all");
    }
  }, [isLoggedIn, initialized]);

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
  };
};

export default useHomeFeed;
