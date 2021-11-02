import { useContext, useEffect } from "react";
import axios from "axios";
import { Container, StepSlider, StepContainer, NextStepButtonWrapper } from "./AddPostPage.style";

import PageLoadingWithCover from "../../components/@layout/PageLoadingWithCover/PageLoadingWithCover";
import AlertPortal from "../../components/@layout/AlertPortal/AlertPortal";
import ConfirmPortal from "../../components/@layout/ConfirmPortal/ConfirmPortal";
import Button from "../../components/@shared/Button/Button";
import RepositorySelector from "../../components/RepositorySelector/RepositorySelector";
import PostContentUploader from "../../components/PostContentUploader/PostContentUploader";
import TagInputForm from "../../components/TagInputForm/TagInputForm";

import useSnackbar from "../../hooks/common/useSnackbar";
import useModal from "../../hooks/common/useModal";
import usePostUpload from "../../hooks/service/usePostUpload";
import usePostAddStep from "../../hooks/service/usePostAddStep";
import useGithubTags from "../../hooks/service/useGithubTags";

import { PAGE_URL } from "../../constants/urls";
import { FAILURE_MESSAGE, SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import { POST_ADD_STEPS } from "../../constants/steps";

import {
  getPostAddValidationMessage,
  isContentEmpty,
  isFilesEmpty,
  isValidContentLength,
  isGithubRepositoryEmpty,
  isValidPostUploadData,
} from "../../utils/postUpload";
import { getAPIErrorMessage } from "../../utils/error";
import { ScrollPageWrapper } from "../../components/@styled/layout";
import HomeFeedContext from "../../contexts/HomeFeedContext";

const AddPostPage = () => {
  const { refetchAll: refetchHomeFeed } = useContext(HomeFeedContext);
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

  const handlePostAddComplete = async () => {
    if (!isValidPostUploadData({ content, githubRepositoryName, tags, files })) {
      showAlert(getPostAddValidationMessage({ content, githubRepositoryName, tags, files }));
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
        throw error;
      }

      showAlert(getAPIErrorMessage(error.response?.data.errorCode));
      deactivateUploadingState();
    }
  };

  const handleNextButtonClick = () => {
    if (stepIndex === 0 && !isGithubRepositoryEmpty(githubRepositoryName)) {
      showAlert(FAILURE_MESSAGE.POST_REPOSITORY_NOT_SELECTED);
      return;
    }

    if (stepIndex === 1 && !isValidContentLength(content)) {
      showAlert(FAILURE_MESSAGE.POST_CONTENT_LENGTH_LIMIT_EXCEEDED);
      return;
    }

    if (stepIndex === 1 && isContentEmpty(content) && isFilesEmpty(files)) {
      showAlert(FAILURE_MESSAGE.POST_FILE_AND_CONTENT_EMPTY);
      return;
    }

    if (stepIndex === 1 && isFilesEmpty(files)) {
      showAlert(FAILURE_MESSAGE.POST_FILE);
      return;
    }

    if (stepIndex === 1 && isContentEmpty(content)) {
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
    <ScrollPageWrapper>
      <Container>
        <StepSlider stepCount={POST_ADD_STEPS.length} stepIndex={stepIndex}>
          {POST_ADD_STEPS.map((STEP, index) => (
            <StepContainer key={STEP.title} stepCount={POST_ADD_STEPS.length} isShown={stepIndex === index}>
              {stepComponents[index]}
            </StepContainer>
          ))}
        </StepSlider>
        <NextStepButtonWrapper>
          {stepIndex < POST_ADD_STEPS.length - 1 ? (
            <Button kind="roundedBlock" onClick={handleNextButtonClick}>
              다음
            </Button>
          ) : (
            <Button kind="roundedBlock" onClick={handlePostAddComplete}>
              작성 완료
            </Button>
          )}
          {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
          {isConfirmShown && (
            <ConfirmPortal heading={confirmMessage} onConfirm={handleConfirm} onCancel={hideConfirm} />
          )}
        </NextStepButtonWrapper>
        {uploading && stepIndex === POST_ADD_STEPS.length - 1 && <PageLoadingWithCover description="게시중" />}
      </Container>
    </ScrollPageWrapper>
  );
};

export default AddPostPage;
