import { STEPS } from "../../constants/steps";
import useStep from "../../services/hooks/@common/useStep";
import StepHeader from "../@layout/StepHeader/StepHeader";

const PostAddStepHeader = () => {
  const { stepIndex, goBack, goNextStep } = useStep(STEPS);

  return (
    <StepHeader isNextStepExist={stepIndex < STEPS.length - 1} onGoBack={goBack} onNextStepClick={goNextStep}>
      {STEPS[stepIndex]}
    </StepHeader>
  );
};

export default PostAddStepHeader;
