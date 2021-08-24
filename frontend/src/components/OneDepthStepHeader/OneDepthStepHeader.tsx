import { useHistory } from "react-router-dom";
import StepHeader from "../@layout/StepHeader/StepHeader";

export interface Props {
  title: string;
}

const OneDepthStepHeader = ({ title }: Props) => {
  const history = useHistory();

  return (
    <StepHeader isNextStepExist={false} onGoBack={history.goBack}>
      {title}
    </StepHeader>
  );
};

export default OneDepthStepHeader;
