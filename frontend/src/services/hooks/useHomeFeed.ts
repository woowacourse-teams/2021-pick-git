import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { useQueryClient } from "react-query";

import { Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError } from "../../utils/api";
import { removeDuplicatedData } from "../../utils/data";
import { useHomeFeedPostsQuery } from "../queries";

const useHomeFeed = () => {
  const { logout } = useContext(UserContext);
  const [posts, setPosts] = useState<Post[]>([]);
  const { data, isLoading, error, isError, isFetching, fetchNextPage } = useHomeFeedPostsQuery();
  const queryClient = useQueryClient();

  const handlePostsEndIntersect = () => {
    fetchNextPage();
  };

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      if (status) {
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
    const fetchedPosts = data?.pages?.reduce((acc, postPage) => acc.concat(postPage), []) ?? [];
    const filteredPosts = removeDuplicatedData<Post>(fetchedPosts, (post) => post.id);

    setPosts(filteredPosts);
  };

  useEffect(() => {
    handleDataFetch();
  }, [data]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { posts, handlePostsEndIntersect, isLoading, isFetching, isError };
};

export default useHomeFeed;
