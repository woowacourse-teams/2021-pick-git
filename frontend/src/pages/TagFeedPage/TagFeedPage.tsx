import { Container } from "./TagFeedPage.style";
import Feed from "../../components/Feed/Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";

const TagFeedPage = () => {
  const queryResult = useHomeFeedPostsQuery();

  return (
    <Container>
      <Feed queryResult={queryResult} />
    </Container>
  );
};

export default TagFeedPage;
