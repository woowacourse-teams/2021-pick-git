import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import PageError from "../../components/@shared/PageError/PageError";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import UserList from "../../components/UserList/UserList";

import { QUERY } from "../../constants/queries";

import useFollowingList from "../../hooks/service/useFollowingList";

import { Container, ContentWrapper } from "./FollowingListPage.style";

const FollowingList = () => {
  const username = new URLSearchParams(location.search).get("username");
  const { results, isError, isLoading, isFetchingNextPage, handleIntersect } = useFollowingList(username);

  if (isLoading) {
    return <PageLoading />;
  }

  if (isError) {
    return <PageError errorMessage="목록을 표시할 수 없습니다" />;
  }

  return (
    <ScrollPageWrapper>
      <Container>
        <ContentWrapper>
          <UserList
            users={results}
            isFetchingNextPage={isFetchingNextPage}
            onIntersect={handleIntersect}
            queryKey={[QUERY.GET_PROFILE_FOLLOWING, { username }]}
          />
        </ContentWrapper>
      </Container>
    </ScrollPageWrapper>
  );
};

export default FollowingList;
