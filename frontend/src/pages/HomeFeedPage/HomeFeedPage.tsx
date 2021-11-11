import { useEffect } from "react";
import Bowser from "bowser";
import PageLoadingWithLogo from "../../components/@layout/PageLoadingWithLogo/PageLoadingWithLogo";
import PageError from "../../components/@shared/PageError/PageError";
import Tabs from "../../components/@shared/Tabs/Tabs";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import Feed from "../../components/Feed/Feed";
import { NOT_FOUND_MESSAGE } from "../../constants/messages";

import { QUERY } from "../../constants/queries";
import useAuth from "../../hooks/common/useAuth";
import useAutoAnchor from "../../hooks/common/useAutoAnchor";

import useInfiniteImagePreloader from "../../hooks/common/useInfiniteImagePreloader";
import useModal from "../../hooks/common/useModal";
import useHomeFeed from "../../hooks/service/useHomeFeed";

import AlertPortal from "../../components/@layout/AlertPortal/AlertPortal";
import { Container, postTabCSS, PostTabWrapper } from "./HomeFeedPage.style";

const HomeFeedPage = () => {
  const { isLoggedIn } = useAuth();
  const {
    infinitePostsData,
    isLoading,
    isFetching,
    isError,
    handlePostsEndIntersect,
    feedFilterOption,
    currentPostId,
    setFeedFilterOption,
    setCurrentPostId,
    refetchAll,
  } = useHomeFeed();
  const { scrollWrapperRef } = useAutoAnchor(`#post${currentPostId}`);
  const {
    modalMessage: alertMessage,
    isModalShown: isAlertShown,
    showModal: showAlert,
    hideModal: hideAlert,
  } = useModal();

  const infiniteImageUrls =
    infinitePostsData?.pages.map<string[]>(
      (posts) => posts?.reduce<string[]>((acc, post) => [...acc, ...post.imageUrls], []) ?? []
    ) ?? [];
  const { isFirstImagesLoading, isImagesFetching, activateImageFetchingState } =
    useInfiniteImagePreloader(infiniteImageUrls);

  const tabList = [
    { name: "All", onTabChange: () => setFeedFilterOption("all") },
    { name: "Followings", onTabChange: () => setFeedFilterOption("followings") },
  ];

  const handleIntersect = () => {
    handlePostsEndIntersect();
    activateImageFetchingState();
  };

  useEffect(() => {
    const browserName = Bowser.getParser(window.navigator.userAgent).getBrowserName().toLowerCase();
    if (browserName === "safari") {
      showAlert("특정 사파리 버전에선 \n 앱의 기능이 제한될 수 있습니다.");
    }
  }, []);

  if (isLoading || isFirstImagesLoading) {
    return <PageLoadingWithLogo />;
  }

  if (isError || !infinitePostsData) {
    return <PageError errorMessage="게시물을 가져올 수 없습니다." />;
  }

  return (
    <ScrollPageWrapper ref={scrollWrapperRef}>
      <Container>
        {isLoggedIn && (
          <PostTabWrapper>
            <Tabs
              tabItems={tabList}
              tabIndicatorKind="line"
              defaultTabIndex={feedFilterOption === "all" ? 0 : 1}
              cssProp={postTabCSS}
            />
          </PostTabWrapper>
        )}
        <Feed
          infinitePostsData={infinitePostsData}
          onIntersect={handleIntersect}
          queryKeyList={[QUERY.GET_HOME_FEED_POSTS("followings"), QUERY.GET_HOME_FEED_POSTS("all")]}
          isFetching={isFetching || isImagesFetching}
          setCurrentPostId={setCurrentPostId}
          notFoundMessage={feedFilterOption === "followings" ? NOT_FOUND_MESSAGE.POSTS.FOLLOWINGS : null}
        />
      </Container>
      {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
    </ScrollPageWrapper>
  );
};

export default HomeFeedPage;
