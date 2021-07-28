import { Dispatch, SetStateAction } from "react";
import { useHistory } from "react-router-dom";
import { Step } from "../../../@types";
import { getLastPath } from "../../../utils/history";

interface Parameters {
  steps: Step[];
  stepIndex: number;
  setStepIndex: Dispatch<SetStateAction<number>>;
}

const useStep = ({ steps, stepIndex, setStepIndex }: Parameters) => {
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

  return {
    stepIndex,
    setStepMoveEventHandler,
    removeStepMoveEventHandler,
    goBack,
    goNextStep,
  };
};

export default useStep;
