import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import PageError from "../../components/@shared/PageError/PageError";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import UserList from "../../components/UserList/UserList";

import { QUERY } from "../../constants/queries";

import useFollowerList from "../../hooks/service/useFollowerList";

import { Container, ContentWrapper } from "./FollowerListPage.style";

const FollowerList = () => {
  const username = new URLSearchParams(location.search).get("username");
  const { results, isError, isLoading, isFetchingNextPage, handleIntersect } = useFollowerList(username);

  if (isLoading) {
    return <PageLoading />;
  }

  if (isError) {
    return <PageError errorMessage="목록을 표시할 수 없습니다." />;
  }

  return (
    <ScrollPageWrapper>
      <Container>
        <ContentWrapper>
          <UserList
            users={results}
            isFetchingNextPage={isFetchingNextPage}
            onIntersect={handleIntersect}
            queryKey={[QUERY.GET_PROFILE_FOLLOWER, { username }]}
          />
        </ContentWrapper>
      </Container>
    </ScrollPageWrapper>
  );
};

export default FollowerList;
