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
  const { postLikePeople, isLoading, isError, refetch } = usePostLikePeople(postId);
  const follow = useFollow();

  if (isError || !postLikePeople) {
    return <div>에러!!</div>;
  }

  if (isLoading) {
    return (
      <Container>
        <PageLoading />
      </Container>
    );
  }

  return (
    <Container>
      <UserList
        key="post-like-user"
        users={postLikePeople}
        isFetchingNextPage={false}
        onIntersect={() => {}}
        follow={follow}
        refetch={refetch}
      />
    </Container>
  );
};

export default PostLikePeoplePage;
