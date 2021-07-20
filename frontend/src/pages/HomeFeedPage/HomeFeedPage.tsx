import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/Feed/Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import axios from "axios";
import { useQueryClient } from "react-query";
import { useContext } from "react";
import UserContext from "../../contexts/UserContext";
import { QUERY } from "../../constants/queries";

const HomeFeedPage = () => {
  const { data, isLoading, error, isFetching, fetchNextPage } = useHomeFeedPostsQuery();
  const queryClient = useQueryClient();
  const { logout } = useContext(UserContext);

  const allPosts = data?.pages?.reduce((acc, postPage) => acc.concat(postPage), []);

  const handlePostsEndIntersect = () => {
    fetchNextPage();
  };

  if (error) {
    if (axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      if (status === 401) {
        logout();
        queryClient.refetchQueries(QUERY.GET_HOME_FEED_POSTS, { active: true });
      }
    }

    return <div>에러!!</div>;
  }

  if (isLoading || !allPosts) {
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
