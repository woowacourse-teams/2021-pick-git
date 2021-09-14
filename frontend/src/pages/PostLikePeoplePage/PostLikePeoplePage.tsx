import { Container, ContentWrapper } from "./PostLikePeoplePage.style";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import usePostLikePeople from "../../services/hooks/usePostLikePeople";
import { useLocation } from "react-router-dom";
import { Post } from "../../@types";
import UserList from "../../components/UserList/UserList";
import { QUERY } from "../../constants/queries";

const PostLikePeoplePage = () => {
  const { state: postId } = useLocation<Post["id"]>();
  const { postLikePeople, isLoading, isError } = usePostLikePeople(postId);

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
      <ContentWrapper>
        <UserList
          key="post-like-user"
          users={postLikePeople}
          isFetchingNextPage={false}
          onIntersect={() => {}}
          queryKey={[QUERY.GET_POST_LIKE_PEOPLE, postId]}
        />
      </ContentWrapper>
    </Container>
  );
};

export default PostLikePeoplePage;
