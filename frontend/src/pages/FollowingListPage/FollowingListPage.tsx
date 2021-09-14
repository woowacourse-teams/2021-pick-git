import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import UserList from "../../components/UserList/UserList";
import { QUERY } from "../../constants/queries";
import useFollowingList from "../../services/hooks/useFollowingList";
import { Container, ContentWrapper } from "./FollowingListPage.style";

const FollowingList = () => {
  const username = new URLSearchParams(location.search).get("username");
  const { results, isError, isLoading, isFetchingNextPage, handleIntersect } = useFollowingList(username);

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
          queryKey={[QUERY.GET_PROFILE_FOLLOWING, { username }]}
        />
      </ContentWrapper>
    </Container>
  );
};

export default FollowingList;
