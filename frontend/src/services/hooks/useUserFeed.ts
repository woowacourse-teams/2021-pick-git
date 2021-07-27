import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { InfiniteData, useQueryClient } from "react-query";
import { useHistory } from "react-router-dom";

import { Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import { PAGE_URL } from "../../constants/urls";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError, HTTPErrorHandler } from "../../utils/api";
import { removeDuplicatedData } from "../../utils/data";
import { useUserPostsQuery } from "../queries";

const useUserFeed = (isMyFeed: boolean, username: string | null, prevData?: InfiniteData<Post[]>) => {
  const [allPosts, setAllPosts] = useState<Post[]>([]);
  const [isAllPostsFetched, setIsAllPostsFetched] = useState(false);
  const { data, isLoading, error, isError, refetch, fetchNextPage, isFetchingNextPage } = useUserPostsQuery(
    isMyFeed,
    username
  );

  const queryClient = useQueryClient();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { logout } = useContext(UserContext);
  const history = useHistory();

  const handleIntersect = () => {
    if (isAllPostsFetched) return;

    fetchNextPage();
  };

  const handleDataFetch = () => {
    if (!data) return;

    const pages = data.pages;
    const lastPage = pages[pages.length - 1];

    if (!lastPage || !lastPage.length) {
      setIsAllPostsFetched(true);
    }

    const fetchedPosts = pages?.reduce((acc, postPage) => acc.concat(postPage), []) ?? [];
    const filteredPosts = removeDuplicatedData<Post>(fetchedPosts, (post) => post.id);

    setAllPosts(filteredPosts);
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

      if (status) {
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
  }, [data]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { allPosts, handleIntersect, isLoading, isError, isFetchingNextPage, data };
};

export default useUserFeed;
