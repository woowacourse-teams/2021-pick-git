import { Container } from "./HomeFeedPage.style";
import Feed from "../../components/Feed/Feed";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {}

const HomeFeedPage = ({}: Props) => {
  return (
    <Container>
      <Feed />
    </Container>
  );
};

export default HomeFeedPage;
