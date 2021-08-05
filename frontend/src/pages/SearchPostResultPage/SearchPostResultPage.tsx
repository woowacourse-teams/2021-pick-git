import { useEffect, useState } from "react";
import { InfiniteData } from "react-query";
import { useLocation } from "react-router-dom";

import { Post } from "../../@types";
import useSearchPostData from "../../services/hooks/useSearchPostData";
import { LayoutInPx } from "../../constants/layout";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import { Container } from "./SearchPostResultPage.style";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import Feed from "../../components/Feed/Feed";

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

  const { infinitePostsData, isError, isLoading, isFetchingNextPage, handleIntersect, queryKey } = useSearchPostData(
    type,
    prevData
  );

  useEffect(() => {
    if (!isMountedOnce) {
      setIsMountedOnce(true);
    }

    const $targetPost = document.querySelector(`#post${postId}`);

    if ($targetPost && $targetPost instanceof HTMLElement) {
      window.scrollTo(0, $targetPost.offsetTop - LayoutInPx.HEADER_HEIGHT);
    }
  }, [postId, isMountedOnce]);

  if (isLoading) {
    return <PageLoading />;
  }

  if (isError || !infinitePostsData) {
    return <div>피드를 가져올 수 없습니다.</div>;
  }

  return (
    <Container>
      <InfiniteScrollContainer isLoaderShown={isFetchingNextPage} onIntersect={handleIntersect}>
        <Feed infinitePostsData={infinitePostsData} queryKey={queryKey} />
      </InfiniteScrollContainer>
    </Container>
  );
};

export default SearchPostResultPage;
