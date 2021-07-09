import { Container, Content, EmptySpace, StepLink } from "./StepHeader.style";
import { GoBackIcon, GoForwardIcon } from "../../../assets/icons";
import { useHistory } from "react-router-dom";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  goForwardLink?: string;
}

const StepHeader = ({ goForwardLink, children }: Props) => {
  const history = useHistory();

  const handleGoBack = () => {
    history.goBack();
  };

  const handleGoForward = () => {
    goForwardLink && history.push(goForwardLink);
  };

  return (
    <Container>
      <StepLink onClick={handleGoBack}>
        <GoBackIcon />
      </StepLink>
      <Content>{children}</Content>
      {goForwardLink ? (
        <StepLink onClick={handleGoForward}>
          <GoForwardIcon />
        </StepLink>
      ) : (
        <EmptySpace />
      )}
    </Container>
  );
};

export default StepHeader;
