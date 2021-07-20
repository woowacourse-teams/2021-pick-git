import { useEffect } from "react";
import { Container, StepSlider, StepContainer, NextStepButtonWrapper } from "./AddPostPage.style";
import { STEPS } from "../../constants/steps";
import RepositorySelector from "../../components/RepositorySelector/RepositorySelector";
import PostContentUploader from "../../components/PostContentUploader/PostContentUploader";
import TagInputForm from "../../components/TagInputForm/TagInputForm";
import Button from "../../components/@shared/Button/Button";
import useStep from "../../services/hooks/@common/useStep";
import { PAGE_URL } from "../../constants/urls";
import usePostUpload from "../../services/hooks/usePostUpload";

const AddPostPage = () => {
  const { stepIndex, goNextStep, setStepMoveEventHandler, removeStepMoveEventHandler, completeStep } = useStep(
    STEPS,
    PAGE_URL.HOME
  );
  const { uploadPost, resetUploadData } = usePostUpload();

  const stepComponents = [
    <RepositorySelector key="repository-selector" />,
    <PostContentUploader key="post-content-uploader" />,
    <TagInputForm key="tag-input-form" />,
  ];

  useEffect(() => {
    setStepMoveEventHandler();
    return removeStepMoveEventHandler;
  }, [stepIndex]);

  const handlePostAddComplete = async () => {
    try {
      await uploadPost();
      resetUploadData();
      completeStep();
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <Container>
      <StepSlider stepCount={STEPS.length} stepIndex={stepIndex}>
        {STEPS.map((STEP, index) => (
          <StepContainer key={STEP} stepCount={STEPS.length} isShown={stepIndex === index}>
            {stepComponents[index]}
          </StepContainer>
        ))}
      </StepSlider>
      <NextStepButtonWrapper>
        {stepIndex < STEPS.length - 1 ? (
          <Button kind="roundedBlock" onClick={goNextStep}>
            다음
          </Button>
        ) : (
          <Button kind="roundedBlock" onClick={handlePostAddComplete}>
            작성 완료
          </Button>
        )}
      </NextStepButtonWrapper>
    </Container>
  );
};

export default AddPostPage;
