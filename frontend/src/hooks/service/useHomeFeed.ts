import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { InfiniteData, useQueryClient } from "react-query";

import { Post } from "../@types";
import { QUERY } from "../constants/queries";
import UserContext from "../contexts/UserContext";
import { removeDuplicatedData } from "../utils/data";
import { handleHTTPError } from "../utils/error";
import { isHttpErrorStatus } from "../utils/typeGuard";
import { useHomeFeedPostsQuery } from "../services/queries";
import useFeedMutation from "./useFeedMutation";

const useHomeFeed = () => {
  const { logout } = useContext(UserContext);
  const { data: infinitePostsData, isLoading, error, isError, isFetching, fetchNextPage } = useHomeFeedPostsQuery();

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
            queryClient.refetchQueries(QUERY.GET_HOME_FEED_POSTS, { active: true });
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

  return { infinitePostsData, isLoading, isFetching, isError, handlePostsEndIntersect };
};

export default useHomeFeed;
