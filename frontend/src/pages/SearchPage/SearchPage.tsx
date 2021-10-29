import { useEffect, useState } from "react";

import Chip from "../../components/@shared/Chip/Chip";
import GridFeed from "../../components/@shared/GridFeed/GridFeed";
import Loader from "../../components/@shared/Loader/Loader";
import Tabs from "../../components/@shared/Tabs/Tabs";
import NotFound from "../../components/@shared/NotFound/NotFound";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import UserList from "../../components/UserList/UserList";

import { QUERY } from "../../constants/queries";
import { PAGE_URL } from "../../constants/urls";

import useSearchKeyword from "../../hooks/common/useSearchKeyword";
import useSearchPostData from "../../hooks/service/useSearchPostData";
import useSearchUserData from "../../hooks/service/useSearchUserData";

import { Container, ContentWrapper, KeywordsWrapper, NotFoundCSS } from "./SearchPage.style";
import { getItemsFromPages } from "../../utils/infiniteData";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";

const tabNames = ["계정", "태그"];
const searchTypeIndex = {
  tags: 1,
};

const isSearchTypeValid = (type: string | null): type is keyof typeof searchTypeIndex =>
  type !== null && type in searchTypeIndex;

const SearchPage = () => {
  const type = new URLSearchParams(location.search).get("type");
  const defaultKeyword = new URLSearchParams(location.search).get("keyword");
  const defaultTabIndex = isSearchTypeValid(type) ? searchTypeIndex[type] : 0;
  const [tabIndex, setTabIndex] = useState(defaultTabIndex);
  const [isFirstMount, setIsFirstMount] = useState(true);

  const { keyword, resetKeyword, changeKeyword } = useSearchKeyword();
  const formattedKeyword = keyword.trim().replace(/,/g, " ").replace(/\s+/g, " ");

  const {
    results: userSearchResults,
    isError: isUserSearchError,
    isLoading: isUserSearchLoading,
    isFetchingNextPage: isUserSearchFetchingNextPage,
    handleIntersect: handleUserSearchIntersect,
  } = useSearchUserData({ keyword, activated: tabIndex === 0 });
  const {
    infinitePostsData: postSearchResults,
    isError: isPostSearchError,
    isLoading: isPostSearchLoading,
    isFetchingNextPage: isPostSearchFetchingNextPage,
    handleIntersect: handlePostSearchIntersect,
  } = useSearchPostData({ keyword: formattedKeyword, type: "tags", activated: tabIndex === 1 });

  useEffect(() => {
    if (defaultKeyword) {
      changeKeyword(defaultKeyword);
    }
  }, [defaultKeyword]);

  useEffect(() => {
    if (isFirstMount) {
      setIsFirstMount(false);

      return;
    }

    resetKeyword();
  }, [tabIndex]);

  const SearchUserResult = () => {
    if (isUserSearchLoading) {
      return <PageLoading />;
    }

    if (isUserSearchError) {
      return <NotFound type="user" message="검색결과를 표시할 수 없습니다." cssProp={NotFoundCSS} />;
    }

    if (userSearchResults.length === 0) {
      return <NotFound type="user" message="일치하는 계정이 없습니다." cssProp={NotFoundCSS} />;
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
    if (keyword.length !== 0 && isPostSearchLoading) {
      return <PageLoading />;
    }

    if (isPostSearchError || !postSearchResults) {
      return <NotFound type="post" message="검색결과를 표시할 수 없습니다." cssProp={NotFoundCSS} />;
    }

    const posts = getItemsFromPages(postSearchResults.pages);

    if (posts && posts.length === 0) {
      return <NotFound type="post" message="일치하는 게시물이 없습니다." cssProp={NotFoundCSS} />;
    }

    return (
      <>
        <KeywordsWrapper>
          {formattedKeyword.split(" ").map((keyword, index) => keyword && <Chip key={index}>{keyword}</Chip>)}
        </KeywordsWrapper>
        <GridFeed
          feedPagePath={PAGE_URL.SEARCH_RESULT_FEED("tags", formattedKeyword)}
          infinitePostsData={postSearchResults}
          isLoading={isPostSearchLoading}
          isError={isPostSearchError}
          isFetchingNextPage={isPostSearchFetchingNextPage}
          handleIntersect={handlePostSearchIntersect}
        />
      </>
    );
  };

  const tabItems = tabNames.map((name, index) => ({ name, onTabChange: () => setTabIndex(index) }));
  const tabContents = [<SearchUserResult key="user" />, <SearchPostResult key="posts" />];

  const Content = ({ tabIndex }: { tabIndex: number }) => {
    return <ContentWrapper>{tabContents[tabIndex]}</ContentWrapper>;
  };

  return (
    <ScrollPageWrapper>
      <Container>
        <Tabs tabItems={tabItems} defaultTabIndex={defaultTabIndex} tabIndicatorKind="line" />
        <Content tabIndex={tabIndex} />
      </Container>
    </ScrollPageWrapper>
  );
};

export default SearchPage;
