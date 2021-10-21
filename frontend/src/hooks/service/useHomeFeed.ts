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

import type { Post, FeedFilterOption } from "../../@types";

const useHomeFeed = (feedFilterOption: FeedFilterOption) => {
  const { logout } = useContext(UserContext);
  const {
    data: infinitePostsData,
    isLoading,
    error,
    isError,
    isFetching,
    fetchNextPage,
    refetch,
  } = useHomeFeedPostsQuery(feedFilterOption);

  // TODO : 그냥 QUERY 만 보내도 되는지 알아보기
  const { setPostsPages } = useFeedMutation([QUERY]);
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
            queryClient.refetchQueries(QUERY.GET_HOME_FEED_POSTS("all"), { active: true });
          },
        });
      }
    }
  };

  const handleDataFetch = () => {
    if (!infinitePostsData) {
      return;
    }

    const filteredPages = infinitePostsData.pages.map((page) => removeDuplicatedData<Post>(page, (page) => page.id));

    setPostsPages(filteredPages);
  };

  useEffect(() => {
    handleDataFetch();
  }, [infinitePostsData]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { infinitePostsData, isLoading, isFetching, isError, handlePostsEndIntersect, refetch };
};

export default useHomeFeed;
