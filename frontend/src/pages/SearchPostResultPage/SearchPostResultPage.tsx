import { useEffect, useRef, useState } from "react";
import { InfiniteData, QueryKey } from "react-query";
import { useLocation } from "react-router-dom";

import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import PageError from "../../components/@shared/PageError/PageError";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import Feed from "../../components/Feed/Feed";

import { LayoutInPx } from "../../constants/layout";
import { QUERY } from "../../constants/queries";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useSearchPostData from "../../hooks/service/useSearchPostData";

import { Container } from "./SearchPostResultPage.style";

import type { Post } from "../../@types";

interface LocationState {
  prevData?: InfiniteData<Post[]>;
  postId?: string;
  queryKey?: QueryKey;
}

const SearchPostResultPage = () => {
  const [isMountedOnce, setIsMountedOnce] = useState(false);
  const [mountCounter, setMountCounter] = useState(0);
  const ScrollWrapperRef = useRef<HTMLDivElement>(null);
  const {
    state: { postId },
  } = useLocation<LocationState>();

  const params = new URLSearchParams(location.search);
  const type = params.get("type") ?? "tags";
  const keyword = params.get("keyword") ?? "";

  const {
    infinitePostsData,
    isError,
    isLoading,
    isFetchingNextPage,
    handleIntersect: handlePostsEndIntersect,
  } = useSearchPostData({
    keyword,
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
      setIsMountedOnce(ScrollWrapperRef.current !== null);

      return;
    }

    const $targetPost = document.querySelector(`#post${postId}`);

    if ($targetPost && $targetPost instanceof HTMLElement) {
      ScrollWrapperRef.current?.scrollTo(0, $targetPost.offsetTop - LayoutInPx.HEADER_HEIGHT);
    }
  }, [postId, mountCounter, isMountedOnce]);

  if (isLoading || isFirstImagesLoading) {
    return <PageLoadingWithLogo />;
  }

  if (isError || !infinitePostsData) {
    return <PageError errorMessage="피드를 가져올 수 없습니다." />;
  }

  return (
    <ScrollPageWrapper ref={ScrollWrapperRef}>
      <Container>
        <InfiniteScrollContainer isLoaderShown={isFetchingNextPage || isImagesFetching} onIntersect={handleIntersect}>
          <Feed
            infinitePostsData={infinitePostsData}
            queryKey={[QUERY.GET_SEARCH_POST_RESULT, { type, keyword }]}
            isFetching={isFetchingNextPage || isImagesFetching}
          />
        </InfiniteScrollContainer>
      </Container>
    </ScrollPageWrapper>
  );
};

export default SearchPostResultPage;
