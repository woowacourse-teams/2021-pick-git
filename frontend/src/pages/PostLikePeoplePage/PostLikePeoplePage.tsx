import { useLocation } from "react-router-dom";

import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import UserList from "../../components/UserList/UserList";

import { QUERY } from "../../constants/queries";

import usePostLikePeople from "../../hooks/service/usePostLikePeople";

import { Container, ContentWrapper } from "./PostLikePeoplePage.style";

import type { Post } from "../../@types";
import { ScrollPageWrapper } from "../../components/@styled/layout";

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
    <ScrollPageWrapper>
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
    </ScrollPageWrapper>
  );
};

export default PostLikePeoplePage;
