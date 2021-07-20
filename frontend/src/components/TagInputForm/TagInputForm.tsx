import { useContext, useEffect } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import UserContext from "../../contexts/UserContext";
import { useGithubTagsQuery } from "../../services/queries";
import PageLoading from "../@layout/PageLoading/PageLoading";
import Chip from "../@shared/Chip/Chip";
import Input from "../@shared/Input/Input";
import { Container, Form, TagList, TagListItem } from "./TagInputForm.style";

const TagInputForm = () => {
  const { currentUsername } = useContext(UserContext);
  const { githubRepositoryName, tags, setTags } = useContext(PostAddDataContext);
  const { data: defaultTags, isLoading, error, refetch } = useGithubTagsQuery(currentUsername, githubRepositoryName);

  useEffect(() => {
    defaultTags && setTags((state) => [...defaultTags, ...state]);
  }, [defaultTags]);

  useEffect(() => {
    if (githubRepositoryName !== "") {
      refetch();
      setTags([]);
    }
  }, [githubRepositoryName]);

  const handleTagSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();
    const newTag = event.currentTarget["tag-input"].value;

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
    </Container>
  );
};

export default TagInputForm;
