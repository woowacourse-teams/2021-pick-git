import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import UserList from "../../components/UserList/UserList";
import useFollow from "../../services/hooks/useFollow";
import useFollowerList from "../../services/hooks/useFollowerList";
import { Container } from "./FollowerList.style";

const FollowerList = () => {
  const username = new URLSearchParams(location.search).get("username");
  const { results, isError, isLoading, isFetchingNextPage, handleIntersect, refetch } = useFollowerList(username);
  const follow = useFollow();

  if (isLoading) {
    return <PageLoading />;
  }

  if (isError) {
    return <div>목록을 표시할 수 없습니다.</div>;
  }

  return (
    <Container>
      <UserList
        users={results}
        isFetchingNextPage={isFetchingNextPage}
        onIntersect={handleIntersect}
        follow={follow}
        refetch={refetch}
      ></UserList>
    </Container>
  );
};

export default FollowerList;
