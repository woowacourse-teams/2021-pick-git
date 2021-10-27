import { InfiniteData, QueryKey } from "react-query";
import { useLocation } from "react-router-dom";

import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import PageError from "../../components/@shared/PageError/PageError";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import Feed from "../../components/Feed/Feed";

import { QUERY } from "../../constants/queries";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useSearchPostData from "../../hooks/service/useSearchPostData";

import { Container } from "./SearchPostResultPage.style";

import type { Post } from "../../@types";
import useAutoAnchor from "../../hooks/common/useAutoAnchor";

interface LocationState {
  prevData?: InfiniteData<Post[]>;
  postId?: string;
  queryKey?: QueryKey;
}

const SearchPostResultPage = () => {
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
  const { scrollWrapperRef } = useAutoAnchor(postId);

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

  if (isLoading || isFirstImagesLoading) {
    return <PageLoadingWithLogo />;
  }

  if (isError || !infinitePostsData) {
    return <PageError errorMessage="피드를 가져올 수 없습니다." />;
  }

  return (
    <ScrollPageWrapper ref={scrollWrapperRef}>
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
