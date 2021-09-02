import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import UserList from "../../components/UserList/UserList";
import useFollow from "../../services/hooks/useFollow";
import useFollowingList from "../../services/hooks/useFollowingList";
import { Container, ContentWrapper } from "./FollowingListPage.style";

const FollowingList = () => {
  const username = new URLSearchParams(location.search).get("username");
  const { results, isError, isLoading, isFetchingNextPage, handleIntersect, refetch } = useFollowingList(username);
  const follow = useFollow();

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
          follow={follow}
          refetch={refetch}
        ></UserList>
      </ContentWrapper>
    </Container>
  );
};

export default FollowingList;
