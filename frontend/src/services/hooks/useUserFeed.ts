import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import { Post } from "../../@types";
import { PAGE_URL } from "../../constants/urls";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { handleHTTPError } from "../../utils/api";
import { removeDuplicatedData } from "../../utils/data";
import { useUserPostsQuery } from "../queries";

const useUserFeed = (isMyFeed: boolean, username: string | null) => {
  const [allPosts, setAllPosts] = useState<Post[]>([]);
  const [isAllPostsFetched, setIsAllPostsFetched] = useState(false);
  const { data, isLoading, error, isError, refetch, fetchNextPage, isFetchingNextPage } = useUserPostsQuery(
    isMyFeed,
    username
  );

  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { isLoggedIn, logout } = useContext(UserContext);
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

      return;
    }

    const fetchedPosts = pages?.reduce((acc, postPage) => acc.concat(postPage), []) ?? [];
    const filteredPosts = removeDuplicatedData<Post>(fetchedPosts, (post) => post.id);

    setAllPosts(filteredPosts);
  };

  const handleError = () => {
    if (!error) return;

    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      const errorHandler = {
        unauthorized: () => {
          if (isMyFeed) {
            pushSnackbarMessage("로그인한 사용자만 사용할 수 있는 서비스입니다.");

            history.push(PAGE_URL.HOME);
          } else {
            isLoggedIn && pushSnackbarMessage("사용자 정보가 유효하지 않아 자동으로 로그아웃합니다.");
            logout();
            refetch();
          }
        },
      };

      if (status) {
        handleHTTPError(status, errorHandler);
      }
    }
  };

  useEffect(() => {
    handleDataFetch();
  }, [data]);

  useEffect(() => {
    handleError();
  }, [error]);

  return { allPosts, handleIntersect, isLoading, isError, isFetchingNextPage };
};

export default useUserFeed;
