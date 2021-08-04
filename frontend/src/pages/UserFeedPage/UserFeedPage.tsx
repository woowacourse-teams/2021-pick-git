import { useContext, useEffect, useState } from "react";

import Feed from "../../components/Feed/Feed";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import useUserFeed from "../../services/hooks/useUserFeed";
import { Container } from "./UserFeedPage.style";
import { InfiniteData } from "react-query";
import { Post } from "../../@types";
import { useLocation } from "react-router-dom";

import UserContext from "../../contexts/UserContext";
import { LayoutInPx } from "../../constants/layout";
import { QUERY } from "../../constants/queries";

interface LocationState {
  prevData?: InfiniteData<Post[]>;
  postId?: string;
}

const UserFeedPage = () => {
  const [isMountedOnce, setIsMountedOnce] = useState(false);
  const { currentUsername } = useContext(UserContext);
  const username = new URLSearchParams(location.search).get("username");
  const isMyFeed = currentUsername === username;

  const {
    state: { prevData, postId },
  } = useLocation<LocationState>();

  const { infinitePostsData, isLoading, isError, isFetchingNextPage, handleIntersect } = useUserFeed(
    isMyFeed,
    username,
    prevData
  );

  useEffect(() => {
    if (!isMountedOnce) {
      setIsMountedOnce(true);
    }

    const $targetPost = document.querySelector(`#post${postId}`);

    if ($targetPost && $targetPost instanceof HTMLElement) {
      window.scrollTo(0, $targetPost.offsetTop - LayoutInPx.HEADER_HEIGHT);
    }
  }, [postId, isMountedOnce]);

  if (isLoading) {
    return <PageLoading />;
  }

  if (isError || !infinitePostsData) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  return (
    <Container>
      <InfiniteScrollContainer isLoaderShown={isFetchingNextPage} onIntersect={handleIntersect}>
        <Feed infinitePostsData={infinitePostsData} queryKey={[QUERY.GET_USER_FEED_POSTS, { username, isMyFeed }]} />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default UserFeedPage;
