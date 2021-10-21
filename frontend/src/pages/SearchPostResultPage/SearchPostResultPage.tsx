import { useEffect, useRef, useState } from "react";
import { InfiniteData, QueryKey } from "react-query";
import { useLocation } from "react-router-dom";

import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import PageError from "../../components/@shared/PageError/PageError";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import Feed from "../../components/Feed/Feed";

import { LayoutInPx } from "../../constants/layout";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useSearchKeyword from "../../hooks/common/useSearchKeyword";
import useSearchPostData from "../../hooks/service/useSearchPostData";

import { Container } from "./SearchPostResultPage.style";

import type { Post } from "../../@types";
import { QUERY } from "../../constants/queries";

interface LocationState {
  prevData?: InfiniteData<Post[]>;
  postId?: string;
  queryKey?: QueryKey;
}

const SearchPostResultPage = () => {
  const [isMountedOnce, setIsMountedOnce] = useState(false);
  const [mountCounter, setMountCounter] = useState(0);
  const containerRef = useRef<any>();
  const { keyword } = useSearchKeyword();
  const type = new URLSearchParams(location.search).get("type") ?? "tags";
  const {
    state: { postId },
  } = useLocation<LocationState>();

  const formattedKeyword = keyword.trim().replace(/,/g, " ").replace(/\s+/g, " ");

  const {
    infinitePostsData,
    isError,
    isLoading,
    isFetchingNextPage,
    handleIntersect: handlePostsEndIntersect,
  } = useSearchPostData({
    keyword: formattedKeyword,
    type,
    activated: true,
  });

  const infiniteImageUrls =
    infinitePostsData?.pages.map(
      (posts) => posts?.reduce<string[]>((acc, post) => [...acc, ...post.imageUrls], []) ?? []
    ) ?? [];
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
  }, [postId, mountCounter, isMountedOnce]);

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
          queryKey={[QUERY.GET_SEARCH_POST_RESULT, { type, keyword: formattedKeyword }]}
          isFetching={isFetchingNextPage || isImagesFetching}
        />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default SearchPostResultPage;
