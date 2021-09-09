import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/Feed/Feed";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import useHomeFeed from "../../services/hooks/useHomeFeed";
import { QUERY } from "../../constants/queries";
import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import useInfiniteImagePreloader from "../../services/hooks/@common/useInfiniteImagePreloader";

const HomeFeedPage = () => {
  const { infinitePostsData, isLoading, isFetching, isError, handlePostsEndIntersect } = useHomeFeed();
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
    return <div>에러!!</div>;
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
