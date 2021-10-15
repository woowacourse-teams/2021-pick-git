import { useContext } from "react";
import { InfiniteData } from "react-query";
import { useHistory } from "react-router-dom";
import { Post, Step } from "../../@types";
import PostEditStepContext from "../../contexts/PostEditStepContext";
import useStep from "../common/useStep";

interface StepCompleteLinkData {
  pathname: string;
  search: string;
  state?: { prevData: InfiniteData<Post[]>; postId: Post["id"] };
}

const usePostEditStep = (steps: Step[], stepCompleteLinkData: StepCompleteLinkData) => {
  const history = useHistory();

  const { stepIndex, setStepIndex } = useContext(PostEditStepContext);

  const completeStep = () => {
    setStepIndex(0);
    stepCompleteLinkData && history.push(stepCompleteLinkData.pathname);
  };

  return {
    ...useStep({ steps, stepIndex, setStepIndex }),
    completeStep,
  };
};

export default usePostEditStep;
