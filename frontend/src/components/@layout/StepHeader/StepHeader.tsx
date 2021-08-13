import { Container, HeaderContent, Content, EmptySpace, StepLink } from "./StepHeader.style";
import { GoBackIcon, GoForwardIcon } from "../../../assets/icons";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  isNextStepExist: boolean;
  onNextStepClick?: () => void;
  onGoBack: () => void;
}

const StepHeader = ({ isNextStepExist, children, onGoBack, onNextStepClick }: Props) => {
  return (
    <Container>
      <HeaderContent>
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
      </HeaderContent>
    </Container>
  );
};

export default StepHeader;
