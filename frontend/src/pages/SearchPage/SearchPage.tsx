import { useContext, useEffect, useState } from "react";

import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import Chip from "../../components/@shared/Chip/Chip";
import GridFeed from "../../components/@shared/GridFeed/GridFeed";
import Tabs from "../../components/@shared/Tabs/Tabs";
import UserList from "../../components/UserList/UserList";
import { QUERY } from "../../constants/queries";
import { PAGE_URL } from "../../constants/urls";
import SearchContext from "../../contexts/SearchContext";
import useSearchPostData from "../../hooks/useSearchPostData";
import useSearchUserData from "../../hooks/useSearchUserData";
import { Container, ContentWrapper, Empty, KeywordsWrapper } from "./SearchPage.style";

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
  const { keyword } = useContext(SearchContext);
  const {
    results: userSearchResults,
    isError: isUserSearchError,
    isLoading: isUserSearchLoading,
    isFetchingNextPage: isUserSearchFetchingNextPage,
    handleIntersect: handleUserSearchIntersect,
    refetch: refetchUserData,
  } = useSearchUserData(tabIndex === 0);
  const {
    infinitePostsData: postSearchResults,
    isError: isPostSearchError,
    isLoading: isPostSearchLoading,
    isFetchingNextPage: isPostSearchFetchingNextPage,
    handleIntersect: handlePostSearchIntersect,
    formattedKeyword: postSearchKeyword,
    refetch: refetchPostData,
  } = useSearchPostData("tags", null, tabIndex === 1);

  useEffect(() => {
    switch (tabIndex) {
      case 0:
        refetchUserData();
        break;
      case 1:
        refetchPostData();
        break;
      default:
        break;
    }
  }, [tabIndex]);

  const SearchUserResult = () => {
    if (isUserSearchLoading) {
      return (
        <Empty>
          <PageLoading />
        </Empty>
      );
    }

    if (isUserSearchError) {
      return <Empty>검색결과를 표시할 수 없습니다.</Empty>;
    }

    if (userSearchResults.length === 0) {
      return <Empty>일치하는 계정이 없습니다.</Empty>;
    }

    return (
      <UserList
        users={userSearchResults}
        isFetchingNextPage={isUserSearchFetchingNextPage}
        onIntersect={handleUserSearchIntersect}
        queryKey={[QUERY.GET_SEARCH_USER_RESULT, { keyword }]}
      />
    );
  };

  const SearchPostResult = () => {
    return (
      <>
        <KeywordsWrapper>
          {postSearchKeyword.split(" ").map((keyword, index) => keyword && <Chip key={index}>{keyword}</Chip>)}
        </KeywordsWrapper>
        {isPostSearchLoading ? (
          <Empty>
            <PageLoading />
          </Empty>
        ) : isPostSearchError ? (
          <Empty>검색결과를 표시할 수 없습니다.</Empty>
        ) : postSearchResults?.pages.length === 0 ? (
          <Empty>게시물이 없습니다.</Empty>
        ) : (
          <GridFeed
            feedPagePath={PAGE_URL.SEARCH_RESULT_FEED("tags")}
            infinitePostsData={postSearchResults}
            isLoading={isPostSearchLoading}
            isError={isPostSearchError}
            isFetchingNextPage={isPostSearchFetchingNextPage}
            handleIntersect={handlePostSearchIntersect}
          />
        )}
      </>
    );
  };

  const tabItems = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [<SearchUserResult key="user" />, <SearchPostResult key="posts" />];

  const Content = ({ tabIndex }: { tabIndex: number }) => {
    return <ContentWrapper>{tabContents[tabIndex]}</ContentWrapper>;
  };

  return (
    <Container>
      <Tabs tabItems={tabItems} defaultTabIndex={defaultTabIndex} tabIndicatorKind="line" />
      <Content tabIndex={tabIndex} />
    </Container>
  );
};

export default SearchPage;
