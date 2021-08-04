import { useContext, useEffect } from "react";
import { Container, StepSlider, StepContainer, NextStepButtonWrapper } from "./AddPostPage.style";
import { POST_ADD_STEPS } from "../../constants/steps";
import RepositorySelector from "../../components/RepositorySelector/RepositorySelector";
import PostContentUploader from "../../components/PostContentUploader/PostContentUploader";
import TagInputForm from "../../components/TagInputForm/TagInputForm";
import Button from "../../components/@shared/Button/Button";
import { PAGE_URL } from "../../constants/urls";
import usePostUpload from "../../services/hooks/usePostUpload";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import MessageModalPortal from "../../components/@layout/MessageModalPortal/MessageModalPortal";
import { FAILURE_MESSAGE, SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import {
  getPostAddValidationMessage,
  isContentEmpty,
  isFilesEmpty,
  isValidContentLength,
  isGithubRepositoryEmpty,
  isValidPostUploadData,
} from "../../utils/postUpload";
import { getAPIErrorMessage } from "../../utils/error";
import usePostAddStep from "../../services/hooks/usePostAddStep";
import { useGithubTagsQuery } from "../../services/queries";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";

const AddPostPage = () => {
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
  } = usePostUpload();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { modalMessage, isModalShown, isCancelButtonShown, showAlertModal, showConfirmModal, hideMessageModal } =
    useMessageModal();
  const tagsQueryResult = useGithubTagsQuery(githubRepositoryName);
  const { currentUsername } = useContext(UserContext);

  const stepComponents = [
    <RepositorySelector
      key="repository-selector"
      setGithubRepositoryName={setGithubRepositoryName}
      goNextStep={goNextStep}
      currentUsername={currentUsername}
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

  useEffect(() => {
    setStepMoveEventHandler();
    return removeStepMoveEventHandler;
  }, [stepIndex]);

  const handlePostAddComplete = async () => {
    if (!isValidPostUploadData({ content, githubRepositoryName, tags, files })) {
      showAlertModal(getPostAddValidationMessage({ content, githubRepositoryName, tags, files }));
      return;
    }

    try {
      await uploadPost();
      resetPostUploadData();
      pushSnackbarMessage(SUCCESS_MESSAGE.POST_ADDED);
      completeStep();
    } catch (error) {
      showAlertModal(getAPIErrorMessage(error.response?.data.errorCode));
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
        {stepIndex < POST_ADD_STEPS.length - 1 ? (
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
    </Container>
  );
};

export default AddPostPage;
