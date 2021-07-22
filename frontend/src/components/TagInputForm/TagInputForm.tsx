import { useEffect } from "react";
import { FAILURE_MESSAGE } from "../../constants/messages";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import usePostUpload from "../../services/hooks/usePostUpload";
import { useGithubTagsQuery } from "../../services/queries";
import { hasDuplicatedTag, isGithubRepositoryEmpty, isValidTagFormat, isValidTagLength } from "../../utils/postUpload";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
import PageLoading from "../@layout/PageLoading/PageLoading";
import Chip from "../@shared/Chip/Chip";
import Input from "../@shared/Input/Input";
import { Container, Form, TagList, TagListItem } from "./TagInputForm.style";

const TagInputForm = () => {
  const { githubRepositoryName, tags, setTags } = usePostUpload();
  const { data: defaultTags, isLoading, error, refetch } = useGithubTagsQuery(githubRepositoryName);
  const { modalMessage, isModalShown, hideMessageModal, showAlertModal } = useMessageModal();

  useEffect(() => {
    defaultTags && setTags((state) => [...defaultTags, ...state]);
  }, [defaultTags]);

  useEffect(() => {
    if (isGithubRepositoryEmpty(githubRepositoryName)) {
      refetch();
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
      showAlertModal(FAILURE_MESSAGE.POST_TAG_LENGTH_LIMIT_EXCEEDED);
      return;
    }

    if (!isValidTagFormat(newTag)) {
      showAlertModal(FAILURE_MESSAGE.POST_TAG_SPECIAL_SYMBOL_EXIST);
      return;
    }

    if (hasDuplicatedTag([...tags, newTag])) {
      showAlertModal(FAILURE_MESSAGE.POST_DUPLICATED_TAG_EXIST);
      return;
    }

    setTags((state) => [...state, newTag]);
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

  if (error) {
    return <div>에러!!</div>;
  }

  if (isLoading) {
    return <PageLoading />;
  }

  return (
    <Container>
      <Form onSubmit={handleTagSubmit}>
        <Input kind="borderBottom" textAlign="center" placeholder="태그 입력..." name="tag-input" />
      </Form>
      <TagList>{tagListItems}</TagList>
      {isModalShown && (
        <MessageModalPortal heading={modalMessage} onConfirm={hideMessageModal} onClose={hideMessageModal} />
      )}
    </Container>
  );
};

export default TagInputForm;
