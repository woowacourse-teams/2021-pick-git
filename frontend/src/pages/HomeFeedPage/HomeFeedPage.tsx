import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/Feed/Feed";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import useHomeFeed from "../../services/hooks/useHomeFeed";

const HomeFeedPage = () => {
  const { allPosts, handlePostsEndIntersect, isLoading, isFetching, isError } = useHomeFeed();

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
        <Feed posts={allPosts} />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default HomeFeedPage;
