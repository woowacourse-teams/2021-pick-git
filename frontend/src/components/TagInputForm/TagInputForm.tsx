import { AxiosError } from "axios";
import { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import { UseQueryResult } from "react-query";
import { useHistory } from "react-router-dom";
import { ErrorResponse, Tags } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import useModal from "../../hooks/common/useModal";
import { getAPIErrorMessage } from "../../utils/error";
import { hasDuplicatedTag, isGithubRepositoryEmpty, isValidTagFormat, isValidTagLength } from "../../utils/postUpload";
import AlertPortal from "../@layout/AlertPortal/AlertPortal";
import PageLoading from "../@layout/PageLoading/PageLoading";
import Chip from "../@shared/Chip/Chip";
import Input from "../@shared/Input/Input";
import SVGIcon from "../@shared/SVGIcon/SVGIcon";
import {
  Container,
  Form,
  TagInputWrapper,
  TagList,
  TagListItem,
  TextLengthIndicator,
  TagAddButton,
  TagInputCSS,
} from "./TagInputForm.style";

interface Props {
  githubRepositoryName: string;
  tags: string[];
  tagsQueryResult?: UseQueryResult<Tags, AxiosError<ErrorResponse>>;
  setTags: Dispatch<SetStateAction<string[]>>;
}

const TagInputForm = ({ githubRepositoryName, tags, tagsQueryResult, setTags }: Props) => {
  const formRef = useRef<HTMLFormElement>(null);
  const [tagInputLength, setTagInputLength] = useState(0);
  const {
    modalMessage: alertMessage,
    isModalShown: isAlertShown,
    showModal: showAlert,
    hideModal: hideAlert,
  } = useModal();
  const history = useHistory();

  useEffect(() => {
    tagsQueryResult?.data && setTags((state) => [...tagsQueryResult.data, ...state]);
  }, [tagsQueryResult?.data]);

  useEffect(() => {
    if (isGithubRepositoryEmpty(githubRepositoryName)) {
      tagsQueryResult && tagsQueryResult.refetch();
      setTags([]);
    }
  }, [githubRepositoryName]);

  const handleTagSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();
    const newTag = event.currentTarget["tag-input"].value;

    if (newTag.length === 0) {
      return;
    }

    if (!isValidTagLength(newTag)) {
      event.currentTarget["tag-input"].blur();
      showAlert(FAILURE_MESSAGE.POST_TAG_LENGTH_LIMIT_EXCEEDED);
      return;
    }

    if (!isValidTagFormat(newTag)) {
      event.currentTarget["tag-input"].blur();
      showAlert(FAILURE_MESSAGE.POST_TAG_SPECIAL_SYMBOL_EXIST);
      return;
    }

    if (hasDuplicatedTag([...tags, newTag])) {
      event.currentTarget["tag-input"].blur();
      showAlert(FAILURE_MESSAGE.POST_DUPLICATED_TAG_EXIST);
      return;
    }

    setTags([...tags, newTag]);
    setTagInputLength(0);

    event.currentTarget["tag-input"].value = "";
  };

  const handleTagDelete = (targetTag: string) => {
    const newTags = tags.filter((tag) => tag !== targetTag);
    setTags(newTags);
  };

  const tagListItems = tags?.map((tag) => (
    <TagListItem key={tag}>
      <Chip onDelete={() => handleTagDelete(tag)}>{tag}</Chip>
    </TagListItem>
  ));

  const handleErrorConfirm = () => {
    history.push(PAGE_URL.HOME);
  };

  const handleTagInputChange: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    if (event.currentTarget.value.length > LIMIT.POST_TAG_LENGTH) {
      event.currentTarget.value = event.currentTarget.value.substr(0, LIMIT.POST_TAG_LENGTH);
    }

    setTagInputLength(event.currentTarget.value.length);
  };

  if (tagsQueryResult && tagsQueryResult.error) {
    tagsQueryResult.error.response && showAlert(getAPIErrorMessage(tagsQueryResult.error.response?.data.errorCode));

    return <AlertPortal heading={alertMessage} onOkay={handleErrorConfirm} />;
  }

  if (tagsQueryResult?.isLoading) {
    return <PageLoading />;
  }

  return (
    <Container>
      <Form onSubmit={handleTagSubmit} ref={formRef}>
        <TagInputWrapper>
          <Input
            kind="borderBottom"
            textAlign="center"
            placeholder="태그 입력..."
            name="tag-input"
            onChange={handleTagInputChange}
            cssProp={TagInputCSS}
          />
          <TagAddButton type="submit">
            <SVGIcon icon="AddBoxIcon" />
          </TagAddButton>
        </TagInputWrapper>
        <TextLengthIndicator>{`${tagInputLength} / ${LIMIT.POST_TAG_LENGTH}`}</TextLengthIndicator>
      </Form>
      <TagList>{tagListItems}</TagList>
      {isAlertShown && <AlertPortal heading={alertMessage} onOkay={hideAlert} />}
    </Container>
  );
};

export default TagInputForm;
