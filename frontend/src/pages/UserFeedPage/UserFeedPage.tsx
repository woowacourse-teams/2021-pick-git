import { Container } from "./UserFeedPage.style";
import Feed from "../../components/Feed/Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";

const UserFeedPage = () => {
  const queryResult = useHomeFeedPostsQuery();

  return (
    <Container>
      <Feed queryResult={queryResult} />
    </Container>
  );
};

export default UserFeedPage;
