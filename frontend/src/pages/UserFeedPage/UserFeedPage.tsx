import { useEffect, useRef, useState } from "react";
import { InfiniteData, useQueryClient } from "react-query";
import { useLocation } from "react-router-dom";

import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageError from "../../components/@shared/PageError/PageError";
import Feed from "../../components/Feed/Feed";

import { LayoutInPx } from "../../constants/layout";
import { QUERY } from "../../constants/queries";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useAuth from "../../hooks/common/useAuth";
import useUserFeed from "../../hooks/service/useUserFeed";

import { Container } from "./UserFeedPage.style";

import type { Post } from "../../@types";

interface LocationState {
  postId?: string;
}

const UserFeedPage = () => {
  const [isMountedOnce, setIsMountedOnce] = useState(false);
  const [mountCounter, setMountCounter] = useState(0);
  const containerRef = useRef<any>();
  const { currentUsername } = useAuth();
  const username = new URLSearchParams(location.search).get("username");
  const isMyFeed = currentUsername === username;

  const {
    state: { postId },
  } = useLocation<LocationState>();

  // TODO : username 이 null 혹은 빈 문자열일 경우에 대한 예외처리
  const {
    infinitePostsData,
    isLoading,
    isError,
    isFetchingNextPage,
    handleIntersect: handlePostsEndIntersect,
  } = useUserFeed(isMyFeed, username);

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
    if (!postId) {
      return;
    }

    if (!isMountedOnce) {
      setMountCounter((prev) => prev + 1);
      setIsMountedOnce(containerRef.current !== undefined);

      return;
    }

    const $targetPost = document.querySelector(`#post${postId}`);

    if ($targetPost && $targetPost instanceof HTMLElement) {
      window.scrollTo(0, $targetPost.offsetTop - LayoutInPx.HEADER_HEIGHT);
    }
  }, [postId, isMountedOnce, mountCounter]);

  if (isLoading || isFirstImagesLoading) {
    return <PageLoadingWithLogo />;
  }

  if (isError || !infinitePostsData) {
    return <PageError errorMessage="피드를 가져올 수 없습니다." />;
  }

  return (
    <Container ref={containerRef}>
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
