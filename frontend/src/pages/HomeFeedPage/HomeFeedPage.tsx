import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/Feed/Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";

const HomeFeedPage = () => {
  const queryResult = useHomeFeedPostsQuery();

  return (
    <Container>
      <Feed queryResult={queryResult} />
    </Container>
  );
};

export default HomeFeedPage;
