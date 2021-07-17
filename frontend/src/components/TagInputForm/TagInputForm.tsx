import { useContext, useEffect } from "react";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import { useGithubTagsQuery } from "../../services/queries";
import Chip from "../@shared/Chip/Chip";
import Input from "../@shared/Input/Input";
import { Container, Form, TagList, TagListItem } from "./TagInputForm.style";

const TagInputForm = () => {
  const { githubRepositoryName, tags, setTags } = useContext(PostAddDataContext);
  const { data: defaultTags, isLoading, error } = useGithubTagsQuery(githubRepositoryName);

  useEffect(() => {
    defaultTags && setTags((state) => [...defaultTags, ...state]);
  }, [defaultTags]);

  const onTagSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();
    const newTag = event.currentTarget["tag-input"].value;

    setTags((state) => [...state, newTag]);
    event.currentTarget["tag-input"].value = "";
  };

  const tagListItems = tags?.map((tag) => (
    <TagListItem>
      <Chip key={tag}>{tag}</Chip>
    </TagListItem>
  ));

  if (error) {
    return <div>에러!!</div>;
  }

  if (isLoading) {
    return <div>로딩중!!</div>;
  }

  return (
    <Container>
      <Form onSubmit={onTagSubmit}>
        <Input kind="borderBottom" textAlign="center" placeholder="태그 입력..." name="tag-input" />
      </Form>
      <TagList>{tagListItems}</TagList>
    </Container>
  );
};

export default TagInputForm;
