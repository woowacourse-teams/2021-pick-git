import { Container } from "./PostLikePeoplePage.style";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import usePostLikePeople from "../../services/hooks/usePostLikePeople";
import { useLocation } from "react-router-dom";
import { Post, UserItem } from "../../@types";
import { getItemsFromPages } from "../../utils/infiniteData";
import UserList from "../../components/UserList/UserList";
import useFollow from "../../services/hooks/useFollow";

const PostLikePeoplePage = () => {
  const { state: postId } = useLocation<Post["id"]>();
  const { infinitePostLikePeople, isLoading, isFetching, isError, getNextPostLikePeople, refetch } =
    usePostLikePeople(postId);
  const follow = useFollow();

  if (isError || !infinitePostLikePeople) {
    return <div>에러!!</div>;
  }

  if (isLoading) {
    return (
      <Container>
        <PageLoading />
      </Container>
    );
  }

  const postLikePeople = getItemsFromPages<UserItem>(infinitePostLikePeople.pages);

  return (
    <Container>
      <UserList
        key="post-like-user"
        users={postLikePeople}
        isFetchingNextPage={isFetching}
        onIntersect={getNextPostLikePeople}
        follow={follow}
        refetch={refetch}
      />
    </Container>
  );
};

export default PostLikePeoplePage;
