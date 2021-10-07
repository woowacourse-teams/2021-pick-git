import { useContext, useEffect } from "react";
import axios from "axios";

import { Container, StepSlider, StepContainer, NextStepButtonWrapper } from "./EditPostPage.style";
import { POST_EDIT_STEPS } from "../../constants/steps";
import PostContentUploader from "../../components/PostContentUploader/PostContentUploader";
import TagInputForm from "../../components/TagInputForm/TagInputForm";
import Button from "../../components/@shared/Button/Button";
import { PAGE_URL } from "../../constants/urls";
import useMessageModal from "../../hooks/common/useMessageModal";
import MessageModalPortal from "../../components/@layout/MessageModalPortal/MessageModalPortal";
import { FAILURE_MESSAGE, SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import {
  getPostEditValidationMessage,
  isContentEmpty,
  isValidContentLength,
  isValidPostEditData,
} from "../../utils/postUpload";
import { getAPIErrorMessage } from "../../utils/error";
import usePostEdit from "../../hooks/service/usePostEdit";
import { useLocation } from "react-router-dom";
import usePostEditStep from "../../hooks/service/usePostEditStep";
import UserContext from "../../contexts/UserContext";
import SnackBarContext from "../../contexts/SnackbarContext";
import PageLoadingWithCover from "../../components/@layout/PageLoadingWithCover/PageLoadingWithCover";

const EditPostPage = () => {
  const { search } = useLocation();
  const { currentUsername } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { postId, content, tags, setTags, setContent, editPost, resetPostEditData, uploading } = usePostEdit();
  const { stepIndex, goNextStep, setStepMoveEventHandler, removeStepMoveEventHandler, completeStep } = usePostEditStep(
    POST_EDIT_STEPS,
    {
      pathname: PAGE_URL.HOME,
      search: `?username=${currentUsername}`,
    }
  );
  const { modalMessage, isModalShown, isCancelButtonShown, showAlertModal, showConfirmModal, hideMessageModal } =
    useMessageModal();

  const stepComponents = [
    <PostContentUploader
      key="post-content-uploader"
      isImageUploaderShown={false}
      content={content}
      setContent={setContent}
    />,
    <TagInputForm key="tag-input-form" githubRepositoryName={search} tags={tags} setTags={setTags} />,
  ];

  useEffect(() => {
    setStepMoveEventHandler();
    return removeStepMoveEventHandler;
  }, [stepIndex]);

  const handlePostAddComplete = async () => {
    if (!isValidPostEditData({ postId, content, tags })) {
      showAlertModal(getPostEditValidationMessage({ postId, content, tags }));
      return;
    }

    try {
      await editPost();
      resetPostEditData();
      pushSnackbarMessage(SUCCESS_MESSAGE.POST_MODIFIED);
      completeStep();
    } catch (error) {
      if (axios.isAxiosError(error)) {
        showAlertModal(getAPIErrorMessage(error.response?.data.errorCode));
      }
    }
  };

  const handleNextButtonClick = () => {
    if (stepIndex === 0 && !isValidContentLength(content)) {
      showAlertModal(FAILURE_MESSAGE.POST_CONTENT_LENGTH_LIMIT_EXCEEDED);
      return;
    }

    if (stepIndex === 0 && isContentEmpty(content)) {
      showConfirmModal(WARNING_MESSAGE.POST_CONTENT_EMPTY);
      return;
    }

    goNextStep();
  };

  const handleConfirmModalConfirm = () => {
    hideMessageModal();
    goNextStep();
  };

  return (
    <Container>
      <StepSlider stepCount={POST_EDIT_STEPS.length} stepIndex={stepIndex}>
        {POST_EDIT_STEPS.map((STEP, index) => (
          <StepContainer key={STEP.title} stepCount={POST_EDIT_STEPS.length} isShown={stepIndex === index}>
            {stepComponents[index]}
          </StepContainer>
        ))}
      </StepSlider>
      <NextStepButtonWrapper>
        {stepIndex < POST_EDIT_STEPS.length - 1 ? (
          <Button kind="roundedBlock" onClick={handleNextButtonClick}>
            다음
          </Button>
        ) : (
          <Button kind="roundedBlock" onClick={handlePostAddComplete}>
            작성 완료
          </Button>
        )}
        {isModalShown && (
          <MessageModalPortal heading={modalMessage} onConfirm={hideMessageModal} onClose={hideMessageModal} />
        )}
        {isModalShown && isCancelButtonShown && (
          <MessageModalPortal
            heading={modalMessage}
            onConfirm={handleConfirmModalConfirm}
            onClose={hideMessageModal}
            onCancel={hideMessageModal}
          />
        )}
      </NextStepButtonWrapper>
      {uploading && <PageLoadingWithCover description="수정중" />}
    </Container>
  );
};

export default EditPostPage;
