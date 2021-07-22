import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { useQueryClient } from "react-query";

import { Post } from "../../@types";
import { QUERY } from "../../constants/queries";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError } from "../../utils/api";
import { useHomeFeedPostsQuery } from "../queries";

const useHomeFeed = () => {
  const { logout } = useContext(UserContext);
  const [allPosts, setAllPosts] = useState<Post[]>([]);
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
    const postIdSet = new Set();
    const filteredPosts = fetchedPosts.filter((post) => {
      const isNewPost = !postIdSet.has(post.id);

      if (isNewPost) {
        postIdSet.add(post.id);
      }

      return isNewPost;
    });

    setAllPosts(filteredPosts);
  };

  useEffect(() => {
    handleDataFetch();
  }, [data]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { allPosts, handlePostsEndIntersect, isLoading, isFetching, isError };
};

export default useHomeFeed;
