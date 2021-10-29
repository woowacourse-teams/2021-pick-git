import { useLocation } from "react-router-dom";

import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import PageError from "../../components/@shared/PageError/PageError";
import Feed from "../../components/Feed/Feed";

import { QUERY } from "../../constants/queries";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useAuth from "../../hooks/common/useAuth";
import useUserFeed from "../../hooks/service/useUserFeed";

import { Container } from "./UserFeedPage.style";
import useAutoAnchor from "../../hooks/common/useAutoAnchor";
import { useState } from "react";
import { Post } from "../../@types";

interface LocationState {
  postId?: string;
}

const UserFeedPage = () => {
  const {
    state: { postId },
  } = useLocation<LocationState>();
  const username = new URLSearchParams(location.search).get("username");

  const [currentPostId, setCurrentPostId] = useState<Post["id"]>(Number(postId) ?? -1);
  const { currentUsername } = useAuth();
  const isMyFeed = currentUsername === username;

  const {
    infinitePostsData,
    isLoading,
    isError,
    isFetchingNextPage,
    handleIntersect: handlePostsEndIntersect,
  } = useUserFeed(isMyFeed, username);
  const { scrollWrapperRef } = useAutoAnchor(`#post${currentPostId}`);

  const infiniteImageUrls =
    infinitePostsData?.pages.map((posts) => posts.reduce<string[]>((acc, post) => [...acc, ...post.imageUrls], [])) ??
    [];

  const { isFirstImagesLoading, isImagesFetching, activateImageFetchingState } =
    useInfiniteImagePreloader(infiniteImageUrls);

  const handleIntersect = () => {
    handlePostsEndIntersect();
    activateImageFetchingState();
  };

  if (isLoading || isFirstImagesLoading) {
    return <PageLoadingWithLogo />;
  }

  if (isError || !infinitePostsData) {
    return <PageError errorMessage="피드를 가져올 수 없습니다." />;
  }

  return (
    <ScrollPageWrapper ref={scrollWrapperRef}>
      <Container>
        <Feed
          infinitePostsData={infinitePostsData}
          onIntersect={handleIntersect}
          queryKeyList={[[QUERY.GET_USER_FEED_POSTS, { username, isMyFeed }]]}
          isFetching={isFetchingNextPage || isImagesFetching}
          setCurrentPostId={setCurrentPostId}
        />
      </Container>
    </ScrollPageWrapper>
  );
};

export default UserFeedPage;
