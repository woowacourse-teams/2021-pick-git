import { Dispatch, SetStateAction } from "react";
import { useHistory } from "react-router-dom";
import { Step } from "../../@types";
import { getLastHash } from "../../utils/history";

interface Parameters {
  steps: Step[];
  stepIndex: number;
  setStepIndex: Dispatch<SetStateAction<number>>;
}

const useStep = ({ steps, stepIndex, setStepIndex }: Parameters) => {
  const history = useHistory();

  const setStepMoveEventHandler = () => {
    window.onpopstate = () => {
      if (getLastHash(history.location.hash) === steps[stepIndex + 1]?.hash) {
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
    history.push({
      hash: steps[stepIndex + 1].hash,
    });
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
