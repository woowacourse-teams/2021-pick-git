import { Container, Content, EmptySpace, StepLink } from "./StepHeader.style";
import { GoBackIcon, GoForwardIcon } from "../../../assets/icons";
import { useHistory } from "react-router-dom";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  isNextStepExist: boolean;
  onNextStepClick?: () => void;
  onGoBack: () => void;
}

const StepHeader = ({ isNextStepExist, children, onGoBack, onNextStepClick }: Props) => {
  return (
    <Container>
      <StepLink onClick={onGoBack}>
        <GoBackIcon />
      </StepLink>
      <Content>{children}</Content>
      {isNextStepExist ? (
        <StepLink onClick={onNextStepClick}>
          <GoForwardIcon />
        </StepLink>
      ) : (
        <EmptySpace />
      )}
    </Container>
  );
};

export default StepHeader;
