import { useEffect, useState } from "react";
import { InfiniteData } from "react-query";
import { useLocation } from "react-router-dom";

import { Post } from "../../@types";
import useSearchPostData from "../../hooks/service/useSearchPostData";
import { LayoutInPx } from "../../constants/layout";
import { Container } from "./SearchPostResultPage.style";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import Feed from "../../components/Feed/Feed";
import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";

interface LocationState {
  prevData?: InfiniteData<Post[]>;
  postId?: string;
}

const SearchPostResultPage = () => {
  const [isMountedOnce, setIsMountedOnce] = useState(false);
  const type = new URLSearchParams(location.search).get("type");
  const {
    state: { prevData, postId },
  } = useLocation<LocationState>();

  const {
    infinitePostsData,
    isError,
    isLoading,
    isFetchingNextPage,
    handleIntersect: handlePostsEndIntersect,
    queryKey,
  } = useSearchPostData(type, prevData);

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
          queryKey={queryKey}
          isFetching={isFetchingNextPage || isImagesFetching}
        />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default SearchPostResultPage;
