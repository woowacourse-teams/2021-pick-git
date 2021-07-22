import { useContext } from "react";
import { useHistory } from "react-router-dom";
import { ThemeContext } from "styled-components";
import { RepositoryIcon, SearchIcon } from "../../assets/icons";
import { REDIRECT_MESSAGE } from "../../constants/messages";
import { STEPS } from "../../constants/steps";
import { PAGE_URL } from "../../constants/urls";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import useStep from "../../services/hooks/@common/useStep";
import usePostUpload from "../../services/hooks/usePostUpload";
import { useGithubRepositoriesQuery } from "../../services/queries";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
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
  const { setGithubRepositoryName } = usePostUpload();
  const { goNextStep } = useStep(STEPS, PAGE_URL.HOME);
  const { data: repositories, isLoading, error } = useGithubRepositoriesQuery();
  const { modalMessage, isModalShown, showAlertModal } = useMessageModal();
  const { color } = useContext(ThemeContext);
  const history = useHistory();

  const handleRepositorySelect = (repositoryName: string) => {
    setGithubRepositoryName(repositoryName);
    goNextStep();
  };

  const goBackToHome = () => {
    history.goBack();
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

  if (repositories?.length === 0) {
    showAlertModal(REDIRECT_MESSAGE.NO_REPOSITORY_EXIST);
  }

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
      {isModalShown && <MessageModalPortal heading={modalMessage} onConfirm={goBackToHome} onClose={goBackToHome} />}
    </Container>
  );
};

export default RepositorySelector;
