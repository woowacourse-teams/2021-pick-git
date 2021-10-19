import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageError from "../../components/@shared/PageError/PageError";
import Feed from "../../components/Feed/Feed";

import { QUERY } from "../../constants/queries";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useHomeFeed from "../../hooks/service/useHomeFeed";

import { Container } from "./HomeFeedPage.style";

const HomeFeedPage = () => {
  const { infinitePostsData, isLoading, isFetching, isError, handlePostsEndIntersect } = useHomeFeed();
  const infiniteImageUrls =
    infinitePostsData?.pages.map<string[]>((posts) =>
      posts.reduce<string[]>((acc, post) => [...acc, ...post.imageUrls], [])
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
    return <PageError errorMessage="게시물을 가져올 수 없습니다." />;
  }

  return (
    <Container>
      <InfiniteScrollContainer isLoaderShown={isFetching || isImagesFetching} onIntersect={handleIntersect}>
        <Feed
          infinitePostsData={infinitePostsData}
          queryKey={[QUERY.GET_HOME_FEED_POSTS]}
          isFetching={isFetching || isImagesFetching}
        />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default HomeFeedPage;
