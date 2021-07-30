import { useState } from "react";

import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import Tabs from "../../components/@shared/Tabs/Tabs";
import SearchListUser from "../../components/SearchListUser/SearchListUser";
import useFollow from "../../services/hooks/useFollow";
import useSearchUserData from "../../services/hooks/useSearchUserData";
import { Container, Empty } from "./SearchPage.style";

const tabNames = ["계정", "태그"];

const SearchPage = () => {
  const [tabIndex, setTabIndex] = useState(0);
  const {
    results: userSearchResults,
    isError: isUserSearchError,
    isLoading: isUserSearchLoading,
    isFetchingNextPage: isUserSearchFetchingNextPage,
    handleIntersect: handleUserSearchIntersect,
    refetch: userSearchRefetch,
  } = useSearchUserData();
  const follow = useFollow();

  const tabItems = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [
    <SearchListUser
      key="user"
      users={userSearchResults}
      isFetchingNextPage={isUserSearchFetchingNextPage}
      onIntersect={handleUserSearchIntersect}
      follow={follow}
      refetch={userSearchRefetch}
    />,
    <Empty key="tag">서비스 준비중입니다.</Empty>,
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
      <Tabs tabItems={tabItems} tabIndicatorKind="line" />
      <Content tabIndex={tabIndex} />
    </Container>
  );
};

export default SearchPage;
