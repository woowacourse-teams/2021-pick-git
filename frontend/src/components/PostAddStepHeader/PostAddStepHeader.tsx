import { POST_ADD_STEPS } from "../../constants/steps";
import usePostAddStep from "../../hooks/service/usePostAddStep";
import StepHeader from "../@layout/StepHeader/StepHeader";

const PostAddStepHeader = () => {
  const { stepIndex, goBack, goNextStep } = usePostAddStep(POST_ADD_STEPS);

  return (
    <StepHeader isNextStepExist={stepIndex < POST_ADD_STEPS.length - 1} onGoBack={goBack} onNextStepClick={goNextStep}>
      {POST_ADD_STEPS[stepIndex].title}
    </StepHeader>
  );
};

export default PostAddStepHeader;
