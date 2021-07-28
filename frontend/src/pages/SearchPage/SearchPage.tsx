import { useState } from "react";

import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import Tabs from "../../components/@shared/Tabs/Tabs";
import SearchListUser from "../../components/SearchListUser/SearchListUser";
import useFollow from "../../services/hooks/useFollow";
import useSearchData from "../../services/hooks/useSearchData";
import { Container } from "./SearchPage.style";

const tabNames = ["계정", "태그"];

const SearchPage = () => {
  const [tabIndex, setTabIndex] = useState(0);
  const { results, isError, isLoading, isFetchingNextPage, handleIntersect, refetch } = useSearchData();
  const follow = useFollow();

  const tabItems = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [
    <SearchListUser
      key="user"
      users={results.users}
      isFetchingNextPage={isFetchingNextPage}
      onIntersect={() => handleIntersect("users")}
      follow={follow}
      refetch={refetch}
    />,
    <div style={{ height: "100%", display: "flex", justifyContent: "center", alignItems: "center" }} key="tag">
      서비스 준비중입니다.
    </div>,
  ];

  if (isLoading) {
    return <PageLoading />;
  }

  if (isError) {
    return (
      <div style={{ height: "100%", display: "flex", justifyContent: "center", alignItems: "center" }} key="tag">
        검색결과를 표시할 수 없습니다.
      </div>
    );
  }

  return (
    <Container>
      <Tabs tabItems={tabItems} tabIndicatorKind="line" />
      {tabContents[tabIndex]}
    </Container>
  );
};

export default SearchPage;
