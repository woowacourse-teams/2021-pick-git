import { useContext } from "react";
import { useHistory } from "react-router-dom";
import { Step } from "../../@types";
import PostEditStepContext from "../../contexts/PostEditStepContext";
import useStep from "./@common/useStep";

const usePostEditStep = (steps: Step[], stepCompleteLink?: string) => {
  const history = useHistory();

  const { stepIndex, setStepIndex } = useContext(PostEditStepContext);

  const completeStep = () => {
    setStepIndex(0);
    stepCompleteLink && history.push(stepCompleteLink);
  };

  return {
    ...useStep({ steps, stepIndex, setStepIndex }),
    completeStep,
  };
};

export default usePostEditStep;
