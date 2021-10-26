import { useEffect, useState } from "react";
import { FeedFilterOption } from "../../@types";

import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import NotFound from "../../components/@shared/NotFound/NotFound";
import PageError from "../../components/@shared/PageError/PageError";
import Tabs from "../../components/@shared/Tabs/Tabs";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import Feed from "../../components/Feed/Feed";

import { QUERY } from "../../constants/queries";
import useAuth from "../../hooks/common/useAuth";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useHomeFeed from "../../hooks/service/useHomeFeed";
import { getItemsFromPages } from "../../utils/infiniteData";

import { Container, NotFoundCSS, postTabCSS, PostTabWrapper } from "./HomeFeedPage.style";

const HomeFeedPage = () => {
  const { isLoggedIn } = useAuth();
  const [feedFilterOption, setFeedFilterOption] = useState<FeedFilterOption>(isLoggedIn ? "followings" : "all");
  const { infinitePostsData, isLoading, isFetching, isError, handlePostsEndIntersect, refetch } =
    useHomeFeed(feedFilterOption);

  const infiniteImageUrls =
    infinitePostsData?.pages.map<string[]>((posts) =>
      posts.reduce<string[]>((acc, post) => [...acc, ...post.imageUrls], [])
    ) ?? [];
  const { isFirstImagesLoading, isImagesFetching, activateImageFetchingState } =
    useInfiniteImagePreloader(infiniteImageUrls);

  const tabList = [
    { name: "Followings", onTabChange: () => setFeedFilterOption("followings") },
    { name: "All", onTabChange: () => setFeedFilterOption("all") },
  ];

  const handleIntersect = () => {
    handlePostsEndIntersect();
    activateImageFetchingState();
  };

  useEffect(() => {
    setFeedFilterOption(isLoggedIn ? "followings" : "all");
  }, [isLoggedIn]);

  useEffect(() => {
    refetch();
  }, [feedFilterOption, refetch]);

  if (isLoading || isFirstImagesLoading) {
    return <PageLoadingWithLogo />;
  }

  if (isError || !infinitePostsData) {
    return <PageError errorMessage="게시물을 가져올 수 없습니다." />;
  }

  const isPostsEmpty = getItemsFromPages(infinitePostsData.pages)?.length === 0;

  return (
    <ScrollPageWrapper>
      <Container>
        {isLoggedIn && (
          <PostTabWrapper>
            <Tabs tabItems={tabList} tabIndicatorKind="line" cssProp={postTabCSS} />
          </PostTabWrapper>
        )}
        {isPostsEmpty ? (
          <NotFound type="post" message="게시글을 올리거나 다른 사람을 팔로우 해보세요" cssProp={NotFoundCSS} />
        ) : (
          <InfiniteScrollContainer isLoaderShown={isFetching || isImagesFetching} onIntersect={handleIntersect}>
            <Feed
              infinitePostsData={infinitePostsData}
              queryKey={[QUERY.GET_HOME_FEED_POSTS]}
              isFetching={isFetching || isImagesFetching}
            />
          </InfiniteScrollContainer>
        )}
      </Container>
    </ScrollPageWrapper>
  );
};

export default HomeFeedPage;
