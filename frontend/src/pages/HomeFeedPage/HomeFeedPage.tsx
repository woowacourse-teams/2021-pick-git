import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/Feed/Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import axios from "axios";
import { useQueryClient } from "react-query";
import { useContext, useEffect, useState } from "react";
import UserContext from "../../contexts/UserContext";
import { QUERY } from "../../constants/queries";
import { Post } from "../../@types";

const HomeFeedPage = () => {
  const { logout } = useContext(UserContext);

  const { data, isLoading, error, isFetching, fetchNextPage } = useHomeFeedPostsQuery();
  const queryClient = useQueryClient();

  const [allPosts, setAllPosts] = useState<Post[]>([]);

  const handlePostsEndIntersect = () => {
    fetchNextPage();
  };

  useEffect(() => {
    const fetchedPosts = data?.pages?.reduce((acc, postPage) => acc.concat(postPage), []) ?? [];
    const postIdSet = new Set();
    const filteredPosts = fetchedPosts.filter((post) => {
      const isNewPost = !postIdSet.has(post.id);

      if (isNewPost) {
        postIdSet.add(post.id);
      }

      return isNewPost;
    });

    setAllPosts(filteredPosts);
  }, [data]);

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
