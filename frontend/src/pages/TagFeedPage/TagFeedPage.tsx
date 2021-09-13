import { Container } from "./TagFeedPage.style";
import Feed from "../../components/Feed/Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import axios from "axios";
import { useQueryClient } from "react-query";
import { useContext } from "react";
import UserContext from "../../contexts/UserContext";
import { QUERY } from "../../constants/queries";
import useInfiniteImagePreloader from "../../services/hooks/@common/useInfiniteImagePreloader";

const TagFeedPage = () => {
  const { data: infinitePostsData, isLoading, error, isFetching, fetchNextPage } = useHomeFeedPostsQuery();
  const queryClient = useQueryClient();
  const { logout } = useContext(UserContext);
  const infiniteImageUrls =
    infinitePostsData?.pages.map((posts) => posts.reduce<string[]>((acc, post) => [...acc, ...post.imageUrls], [])) ??
    [];
  const { isFirstImagesLoading, isImagesFetching, activateImageFetchingState } =
    useInfiniteImagePreloader(infiniteImageUrls);

  const handleIntersect = () => {
    fetchNextPage();
    activateImageFetchingState();
  };

  if (error || !infinitePostsData) {
    if (error && axios.isAxiosError(error)) {
      const { status } = error.response ?? {};

      if (status === 401) {
        logout();
        queryClient.refetchQueries(QUERY.GET_HOME_FEED_POSTS, { active: true });
      }
    }

    return <div>에러!!</div>;
  }

  if (isLoading || isFirstImagesLoading) {
    return <PageLoading />;
  }

  return (
    <Container>
      <InfiniteScrollContainer isLoaderShown={isFetching || isImagesFetching} onIntersect={handleIntersect}>
        <Feed infinitePostsData={infinitePostsData} queryKey="" isFetching={isFetching || isImagesFetching} />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default TagFeedPage;
