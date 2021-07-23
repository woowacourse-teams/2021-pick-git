import { useContext } from "react";
import { useHistory } from "react-router-dom";
import { Step } from "../../../@types";
import PostAddStepContext from "../../../contexts/PostAddStepContext";
import { getLastPath } from "../../../utils/history";

const useStep = (steps: Step[], stepCompleteLink?: string) => {
  const { stepIndex, setStepIndex } = useContext(PostAddStepContext);
  const history = useHistory();

  const setStepMoveEventHandler = () => {
    window.onpopstate = () => {
      if (getLastPath(history.location.pathname) === steps[stepIndex + 1]?.path) {
        setStepIndex(stepIndex + 1);
        return;
      }

      if (stepIndex <= 0) {
        return;
      }

      setStepIndex(stepIndex - 1);
    };
  };

  const removeStepMoveEventHandler = () => {
    window.onpopstate = null;
  };

  const goBack = () => {
    history.goBack();
  };

  const goNextStep = () => {
    setStepIndex(stepIndex + 1);
    history.push(steps[stepIndex + 1].path);
  };

  const completeStep = () => {
    setStepIndex(0);
    stepCompleteLink && history.push(stepCompleteLink);
  };

  return {
    stepIndex,
    setStepMoveEventHandler,
    removeStepMoveEventHandler,
    goBack,
    goNextStep,
    completeStep,
  };
};

export default useStep;
