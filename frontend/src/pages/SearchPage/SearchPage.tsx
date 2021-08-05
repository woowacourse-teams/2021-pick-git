import { useState } from "react";

import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import Chip from "../../components/@shared/Chip/Chip";
import GridFeed from "../../components/@shared/GridFeed/GridFeed";
import Tabs from "../../components/@shared/Tabs/Tabs";
import UserList from "../../components/UserList/UserList";
import { PAGE_URL } from "../../constants/urls";
import useFollow from "../../services/hooks/useFollow";
import useSearchPostData from "../../services/hooks/useSearchPostData";
import useSearchUserData from "../../services/hooks/useSearchUserData";
import { Container, Empty, KeywordsWrapper } from "./SearchPage.style";

const tabNames = ["계정", "태그"];
const searchTypeIndex = {
  tags: 1,
};

const isSearchTypeValid = (type: string | null): type is keyof typeof searchTypeIndex =>
  type !== null && type in searchTypeIndex;

const SearchPage = () => {
  const type = new URLSearchParams(location.search).get("type");
  const defaultTabIndex = isSearchTypeValid(type) ? searchTypeIndex[type] : 0;
  const [tabIndex, setTabIndex] = useState(defaultTabIndex);
  const {
    results: userSearchResults,
    isError: isUserSearchError,
    isLoading: isUserSearchLoading,
    isFetchingNextPage: isUserSearchFetchingNextPage,
    handleIntersect: handleUserSearchIntersect,
    refetch: userSearchRefetch,
  } = useSearchUserData();
  const {
    infinitePostsData: postSearchResults,
    isError: isPostSearchError,
    isLoading: isPostSearchLoading,
    isFetchingNextPage: isPostSearchFetchingNextPage,
    handleIntersect: handlePostSearchIntersect,
    formattedKeyword: postSearchKeyword,
  } = useSearchPostData("tags");
  const follow = useFollow();

  const tabItems = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [
    <UserList
      key="user"
      users={userSearchResults}
      isFetchingNextPage={isUserSearchFetchingNextPage}
      onIntersect={handleUserSearchIntersect}
      follow={follow}
      refetch={userSearchRefetch}
    />,
    <>
      <KeywordsWrapper>
        {postSearchKeyword.split(" ").map((keyword, index) => keyword && <Chip key={index}>{keyword}</Chip>)}
      </KeywordsWrapper>
      <GridFeed
        key="posts"
        feedPagePath={PAGE_URL.SEARCH_RESULT_POST("tags")}
        infinitePostsData={postSearchResults}
        isLoading={isPostSearchLoading}
        isError={isPostSearchError}
        isFetchingNextPage={isPostSearchFetchingNextPage}
        handleIntersect={handlePostSearchIntersect}
      />
    </>,
  ];

  const Content = ({ tabIndex }: { tabIndex: number }) => {
    if (isUserSearchLoading) {
      return <PageLoading />;
    }

    if (isUserSearchError) {
      return <Empty>검색결과를 표시할 수 없습니다.</Empty>;
    }

    return tabContents[tabIndex];
  };

  return (
    <Container>
      <Tabs tabItems={tabItems} defaultTabIndex={defaultTabIndex} tabIndicatorKind="line" />
      <Content tabIndex={tabIndex} />
    </Container>
  );
};

export default SearchPage;
