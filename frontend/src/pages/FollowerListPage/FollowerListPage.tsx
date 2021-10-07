import PageLoading from "../../components/@layout/PageLoading/PageLoading";
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
    return <div>목록을 표시할 수 없습니다.</div>;
  }

  return (
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
  );
};

export default FollowerList;
