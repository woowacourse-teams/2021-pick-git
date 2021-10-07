import { useContext } from "react";
import { useHistory } from "react-router-dom";
import { Step } from "../@types";
import PostAddStepContext from "../contexts/PostAddStepContext";
import useStep from "./common/useStep";

const usePostAddStep = (steps: Step[], stepCompleteLink?: string) => {
  const history = useHistory();

  const { stepIndex, setStepIndex } = useContext(PostAddStepContext);

  const completeStep = () => {
    setStepIndex(0);
    stepCompleteLink && history.push(stepCompleteLink);
  };

  return {
    ...useStep({ steps, stepIndex, setStepIndex }),
    completeStep,
  };
};

export default usePostAddStep;
