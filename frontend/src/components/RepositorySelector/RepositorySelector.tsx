import { useContext } from "react";
import { ThemeContext } from "styled-components";
import { RepositoryIcon, SearchIcon } from "../../assets/icons";
import { STEPS } from "../../constants/steps";
import { PAGE_URL } from "../../constants/urls";
import PostAddDataContext from "../../contexts/PostAddDataContext";
import UserContext from "../../contexts/UserContext";
import useStep from "../../services/hooks/@common/useStep";
import { useGithubRepositoriesQuery } from "../../services/queries";
import PageLoading from "../@layout/PageLoading/PageLoading";
import CircleIcon from "../@shared/CircleIcon/CircleIcon";
import Input from "../@shared/Input/Input";
import {
  Container,
  SearchInputWrapper,
  RepositoryList,
  RepositoryListItem,
  RepositoryCircle,
  RepositoryName,
} from "./RepositorySelector.style";

const RepositorySelector = () => {
  const { currentUsername } = useContext(UserContext);
  const { setGithubRepositoryName } = useContext(PostAddDataContext);
  const { goNextStep } = useStep(STEPS, PAGE_URL.HOME);
  const { data: repositories, isLoading, error } = useGithubRepositoriesQuery(currentUsername);
  const { color } = useContext(ThemeContext);

  const handleRepositorySelect = (repositoryName: string) => {
    setGithubRepositoryName(repositoryName);
    goNextStep();
  };

  const repositoryListItems = repositories?.map((repository) => (
    <RepositoryListItem key={repository.name} onClick={() => handleRepositorySelect(repository.name)}>
      <RepositoryCircle>
        <CircleIcon diameter="1.875rem" backgroundColor={color.tertiaryColor}>
          <RepositoryIcon />
        </CircleIcon>
      </RepositoryCircle>
      <RepositoryName>{repository.name}</RepositoryName>
    </RepositoryListItem>
  ));

  if (error) {
    return <div>에러!!</div>;
  }

  if (isLoading) {
    return <PageLoading />;
  }

  return (
    <Container>
      <SearchInputWrapper>
        <Input kind="borderBottom" icon={<SearchIcon />} />
      </SearchInputWrapper>
      <RepositoryList>{repositoryListItems}</RepositoryList>
    </Container>
  );
};

export default RepositorySelector;
