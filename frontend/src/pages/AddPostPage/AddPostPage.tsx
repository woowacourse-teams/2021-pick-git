import axios from "axios";
import { useEffect } from "react";

import MessageModalPortal from "../../components/@layout/MessageModalPortal/MessageModalPortal";
import PageLoadingWithCover from "../../components/@layout/PageLoadingWithCover/PageLoadingWithCover";
import Button from "../../components/@shared/Button/Button";
import PostContentUploader from "../../components/PostContentUploader/PostContentUploader";
import RepositorySelector from "../../components/RepositorySelector/RepositorySelector";
import TagInputForm from "../../components/TagInputForm/TagInputForm";

import { FAILURE_MESSAGE, SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import { POST_ADD_STEPS } from "../../constants/steps";
import { PAGE_URL } from "../../constants/urls";

import useMessageModal from "../../hooks/common/useMessageModal";
import useSnackbar from "../../hooks/common/useSnackbar";
import useGithubTags from "../../hooks/service/useGithubTags";
import usePostAddStep from "../../hooks/service/usePostAddStep";
import usePostUpload from "../../hooks/service/usePostUpload";

import { getAPIErrorMessage } from "../../utils/error";
import {
  getPostAddValidationMessage,
  isContentEmpty,
  isFilesEmpty, isGithubRepositoryEmpty, isValidContentLength, isValidPostUploadData
} from "../../utils/postUpload";

import { Container, NextStepButtonWrapper, StepContainer, StepSlider } from "./AddPostPage.style";

const AddPostPage = () => {
  const { pushSnackbarMessage } = useSnackbar();
  const { modalMessage, isModalShown, isCancelButtonShown, showAlertModal, showConfirmModal, hideMessageModal } =
    useMessageModal();

  const { stepIndex, goNextStep, setStepMoveEventHandler, removeStepMoveEventHandler, completeStep } = usePostAddStep(
    POST_ADD_STEPS,
    PAGE_URL.HOME
  );
  const {
    content,
    githubRepositoryName,
    tags,
    files,
    setContent,
    setFiles,
    setGithubRepositoryName,
    setTags,
    uploadPost,
    resetPostUploadData,
    uploading,
    activateUploadingState,
    deactivateUploadingState,
  } = usePostUpload();
  const tagsQueryResult = useGithubTags(githubRepositoryName);
  const isPosting = uploading && stepIndex === POST_ADD_STEPS.length - 1;
  const isLastStepIndex = stepIndex < POST_ADD_STEPS.length - 1;

  const handlePostAddComplete = async () => {
    if (!isValidPostUploadData({ content, githubRepositoryName, tags, files })) {
      showAlertModal(getPostAddValidationMessage({ content, githubRepositoryName, tags, files }));
      return;
    }

    try {
      activateUploadingState();
      await uploadPost();

      deactivateUploadingState();
      resetPostUploadData();
      pushSnackbarMessage(SUCCESS_MESSAGE.POST_ADDED);
      completeStep();
    } catch (error) {
      if (!axios.isAxiosError(error)) {
        return;
      }

      showAlertModal(getAPIErrorMessage(error.response?.data.errorCode));
      deactivateUploadingState();
    }
  };

  const handleNextButtonClick = () => {
    if (stepIndex === 0 && !isGithubRepositoryEmpty(githubRepositoryName)) {
      showAlertModal(FAILURE_MESSAGE.POST_REPOSITORY_NOT_SELECTED);
      return;
    }

    if (stepIndex === 1 && !isValidContentLength(content)) {
      showAlertModal(FAILURE_MESSAGE.POST_CONTENT_LENGTH_LIMIT_EXCEEDED);
      return;
    }

    if (stepIndex === 1 && isContentEmpty(content) && isFilesEmpty(files)) {
      showAlertModal(FAILURE_MESSAGE.POST_FILE_AND_CONTENT_EMPTY);
      return;
    }

    if (stepIndex === 1 && isFilesEmpty(files)) {
      showAlertModal(FAILURE_MESSAGE.POST_FILE);
      return;
    }

    if (stepIndex === 1 && isContentEmpty(content)) {
      showConfirmModal(WARNING_MESSAGE.POST_CONTENT_EMPTY);
      return;
    }

    goNextStep();
  };

  const handleConfirmModalConfirm = () => {
    hideMessageModal();
    goNextStep();
  };

  useEffect(() => {
    setStepMoveEventHandler();
    return removeStepMoveEventHandler;
  }, [stepIndex]);

  const stepComponents = [
    <RepositorySelector
      key="repository-selector"
      setGithubRepositoryName={setGithubRepositoryName}
      goNextStep={goNextStep}
    />,
    <PostContentUploader
      key="post-content-uploader"
      isImageUploaderShown={true}
      content={content}
      setContent={setContent}
      setFiles={setFiles}
    />,
    <TagInputForm
      key="tag-input-form"
      tagsQueryResult={tagsQueryResult}
      githubRepositoryName={githubRepositoryName}
      tags={tags}
      setTags={setTags}
    />,
  ];

  return (
    <Container>
      <StepSlider stepCount={POST_ADD_STEPS.length} stepIndex={stepIndex}>
        {POST_ADD_STEPS.map((STEP, index) => (
          <StepContainer key={STEP.title} stepCount={POST_ADD_STEPS.length} isShown={stepIndex === index}>
            {stepComponents[index]}
          </StepContainer>
        ))}
      </StepSlider>
      <NextStepButtonWrapper>
        {isLastStepIndex ? (
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
      {isPosting && <PageLoadingWithCover description="게시중" />}
    </Container>
  );
};

export default AddPostPage;
