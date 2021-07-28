import { useState } from "react";

import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import Tabs from "../../components/@shared/Tabs/Tabs";
import SearchListUser from "../../components/SearchListUser/SearchListUser";
import useSearchData from "../../services/hooks/useSearchData";
import { Container } from "./SearchPage.style";

const tabNames = ["계정", "태그"];

const SearchPage = () => {
  const [tabIndex, setTabIndex] = useState(0);
  const { results, isError, isLoading, isFetchingNextPage, handleIntersect } = useSearchData();

  const tabItems = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [
    <SearchListUser
      key="user"
      users={results.users}
      isFetchingNextPage={isFetchingNextPage}
      onIntersect={() => handleIntersect("users")}
    />,
    <div style={{ height: "100%", display: "flex", justifyContent: "center", alignItems: "center" }} key="tag">
      서비스 준비중입니다.
    </div>,
  ];

  if (isLoading) {
    return <PageLoading />;
  }

  return (
    <Container>
      <Tabs tabItems={tabItems} tabIndicatorKind="line" />
      {tabContents[tabIndex]}
    </Container>
  );
};

export default SearchPage;
