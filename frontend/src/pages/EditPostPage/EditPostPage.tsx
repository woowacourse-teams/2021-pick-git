import { useEffect } from "react";
import { useLocation } from "react-router-dom";

import AlertPortal from "../../components/@layout/AlertPortal/AlertPortal";
import ConfirmPortal from "../../components/@layout/ConfirmPortal/ConfirmPortal";
import PageLoadingWithCover from "../../components/@layout/PageLoadingWithCover/PageLoadingWithCover";
import Button from "../../components/@shared/Button/Button";
import PostContentUploader from "../../components/PostContentUploader/PostContentUploader";
import TagInputForm from "../../components/TagInputForm/TagInputForm";
import { FAILURE_MESSAGE, SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import { POST_EDIT_STEPS } from "../../constants/steps";
import { PAGE_URL } from "../../constants/urls";

import useModal from "../../hooks/common/useModal";
import useSnackbar from "../../hooks/common/useSnackbar";
import useAuth from "../../hooks/common/useAuth";
import usePostEdit from "../../hooks/service/usePostEdit";
import usePostEditStep from "../../hooks/service/usePostEditStep";

import { getAPIErrorMessage } from "../../utils/error";
import {
  getPostEditValidationMessage,
  isContentEmpty,
  isValidContentLength,
  isValidPostEditData,
} from "../../utils/postUpload";

import { Container, NextStepButtonWrapper, StepContainer, StepSlider } from "./EditPostPage.style";
import axios from "axios";
import { ScrollPageWrapper } from "../../components/@styled/layout";

const EditPostPage = () => {
  const { search } = useLocation();

  const { currentUsername } = useAuth();
  const { pushSnackbarMessage } = useSnackbar();

  const {
    modalMessage: alertMessage,
    isModalShown: isAlertShown,
    showModal: showAlert,
    hideModal: hideAlert,
  } = useModal();
  const {
    modalMessage: confirmMessage,
    isModalShown: isConfirmShown,
    showModal: showConfirm,
    hideModal: hideConfirm,
  } = useModal();

  const { postId, content, tags, setTags, setContent, editPost, resetPostEditData, uploading } = usePostEdit();
  const { stepIndex, goNextStep, setStepMoveEventHandler, removeStepMoveEventHandler, completeStep } = usePostEditStep(
    POST_EDIT_STEPS,
    {
      pathname: PAGE_URL.HOME,
      search: `?username=${currentUsername}`,
    }
  );

  const isLastStep = stepIndex >= POST_EDIT_STEPS.length - 1;

  const handlePostAddComplete = async () => {
    if (!isValidPostEditData({ postId, content, tags })) {
      showAlert(getPostEditValidationMessage({ postId, content, tags }));
      return;
    }

    try {
      await editPost();
      resetPostEditData();
      pushSnackbarMessage(SUCCESS_MESSAGE.POST_MODIFIED);
      completeStep();
    } catch (error) {
      if (!axios.isAxiosError(error)) {
        throw error;
      }

      showAlert(getAPIErrorMessage(error.response?.data.errorCode));
    }
  };

  const handleNextButtonClick = () => {
    if (stepIndex === 0 && !isValidContentLength(content)) {
      showAlert(FAILURE_MESSAGE.POST_CONTENT_LENGTH_LIMIT_EXCEEDED);
      return;
    }

    if (stepIndex === 0 && isContentEmpty(content)) {
      showConfirm(WARNING_MESSAGE.POST_CONTENT_EMPTY);
      return;
    }

    goNextStep();
  };

  const handleConfirm = () => {
    hideConfirm();
    goNextStep();
  };

  useEffect(() => {
    setStepMoveEventHandler();
    return removeStepMoveEventHandler;
  }, [stepIndex]);

  const stepComponents = [
    <PostContentUploader
      key="post-content-uploader"
      isImageUploaderShown={false}
      content={content}
      setContent={setContent}
    />,
    <TagInputForm key="tag-input-form" githubRepositoryName={search} tags={tags} setTags={setTags} />,
  ];

  return (
    <ScrollPageWrapper>
      <Container>
        <StepSlider stepCount={POST_EDIT_STEPS.length} stepIndex={stepIndex}>
          {POST_EDIT_STEPS.map((STEP, index) => (
            <StepContainer key={STEP.title} stepCount={POST_EDIT_STEPS.length} isShown={stepIndex === index}>
              {stepComponents[index]}
            </StepContainer>
          ))}
        </StepSlider>
        <NextStepButtonWrapper>
          {isLastStep ? (
            <Button kind="roundedBlock" onClick={handlePostAddComplete}>
              작성 완료
            </Button>
          ) : (
            <Button kind="roundedBlock" onClick={handleNextButtonClick}>
              다음
            </Button>
          )}
          {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
          {isConfirmShown && (
            <ConfirmPortal heading={confirmMessage} onConfirm={handleConfirm} onCancel={hideConfirm} />
          )}
        </NextStepButtonWrapper>
        {uploading && <PageLoadingWithCover description="수정중" />}
      </Container>
    </ScrollPageWrapper>
  );
};

export default EditPostPage;
