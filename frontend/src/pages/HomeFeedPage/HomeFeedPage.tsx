import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/HomeFeed/HomeFeed";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import useHomeFeed from "../../services/hooks/useHomeFeed";
import { QUERY } from "../../constants/queries";

const HomeFeedPage = () => {
  const { posts, handlePostsEndIntersect, isLoading, isFetching, isError } = useHomeFeed();

  if (isError) {
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
        <Feed posts={posts} queryKey={QUERY.GET_HOME_FEED_POSTS} />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default HomeFeedPage;
