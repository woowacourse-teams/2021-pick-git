import { useContext, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { Step } from "../../@types";
import PostAddStepContext from "../../contexts/PostAddStepContext";
import useStep from "../common/useStep";

const usePostAddStep = (steps: Step[], stepCompleteLink?: string) => {
  const history = useHistory();

  const { stepIndex, setStepIndex } = useContext(PostAddStepContext);

  const completeStep = () => {
    setStepIndex(0);
    stepCompleteLink && history.push(stepCompleteLink);
  };

  useEffect(() => {
    const hash = new URL(location.href).hash.substr(1);
    const currentStepIndex = steps.findIndex((step) => step.hash === hash);
    if (currentStepIndex === -1) {
      return;
    }

    setStepIndex(currentStepIndex);
  }, []);

  return {
    ...useStep({ steps, stepIndex, setStepIndex }),
    completeStep,
  };
};

export default usePostAddStep;
