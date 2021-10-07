import { useContext, useEffect, useState } from "react";
import { InfiniteData } from "react-query";
import { useLocation } from "react-router-dom";

import Feed from "../../components/Feed/Feed";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import useUserFeed from "../../hooks/useUserFeed";
import { Container } from "./UserFeedPage.style";
import { Post } from "../../@types";

import UserContext from "../../contexts/UserContext";
import { LayoutInPx } from "../../constants/layout";
import { QUERY } from "../../constants/queries";
import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";

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

  const {
    infinitePostsData,
    isLoading,
    isError,
    isFetchingNextPage,
    handleIntersect: handlePostsEndIntersect,
  } = useUserFeed(isMyFeed, username, prevData);

  const infiniteImageUrls =
    infinitePostsData?.pages.map((posts) => posts.reduce<string[]>((acc, post) => [...acc, ...post.imageUrls], [])) ??
    [];
  const { isFirstImagesLoading, isImagesFetching, activateImageFetchingState } =
    useInfiniteImagePreloader(infiniteImageUrls);

  const handleIntersect = () => {
    handlePostsEndIntersect();
    activateImageFetchingState();
  };

  useEffect(() => {
    if (!isMountedOnce) {
      setIsMountedOnce(true);
    }

    const $targetPost = document.querySelector(`#post${postId}`);

    if ($targetPost && $targetPost instanceof HTMLElement) {
      window.scrollTo(0, $targetPost.offsetTop - LayoutInPx.HEADER_HEIGHT);
    }
  }, [postId, isMountedOnce]);

  if (isLoading || isFirstImagesLoading) {
    return <PageLoadingWithLogo />;
  }

  if (isError || !infinitePostsData) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  return (
    <Container>
      <InfiniteScrollContainer isLoaderShown={isFetchingNextPage || isImagesFetching} onIntersect={handleIntersect}>
        <Feed
          infinitePostsData={infinitePostsData}
          queryKey={[QUERY.GET_USER_FEED_POSTS, { username, isMyFeed }]}
          isFetching={isFetchingNextPage || isImagesFetching}
        />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default UserFeedPage;
