import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/Feed/Feed";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import useHomeFeed from "../../services/hooks/useHomeFeed";
import { QUERY } from "../../constants/queries";

const HomeFeedPage = () => {
  const { infinitePostsData, isLoading, isFetching, isError, handlePostsEndIntersect } = useHomeFeed();

  if (isError || !infinitePostsData) {
    return <div>에러!!</div>;
  }

  if (isLoading) {
    return (
      <Container>
        <PageLoading />
      </Container>
    );
  }

  return (
    <Container>
      <InfiniteScrollContainer isLoaderShown={isFetching} onIntersect={handlePostsEndIntersect}>
        <Feed infinitePostsData={infinitePostsData} queryKey={[QUERY.GET_HOME_FEED_POSTS]} />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default HomeFeedPage;
