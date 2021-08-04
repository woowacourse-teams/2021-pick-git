import { Dispatch, SetStateAction, useContext, useState } from "react";
import { useHistory } from "react-router-dom";
import { ThemeContext } from "styled-components";
import { RepositoryIcon, SearchIcon } from "../../assets/icons";
import { REDIRECT_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import { useGithubRepositoriesQuery } from "../../services/queries";
import { getAPIErrorMessage } from "../../utils/error";
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

interface Props {
  currentUsername: string;
  setGithubRepositoryName: Dispatch<SetStateAction<string>>;
  goNextStep: () => void;
}

const RepositorySelector = ({ currentUsername, setGithubRepositoryName, goNextStep }: Props) => {
  const [searchText, setSearchText] = useState("");
  const { data: repositories, isLoading, error } = useGithubRepositoriesQuery(currentUsername);
  const { modalMessage, isModalShown, showAlertModal, hideMessageModal } = useMessageModal();
  const { color } = useContext(ThemeContext);
  const history = useHistory();

  const handleRepositorySelect = (repositoryName: string) => {
    setGithubRepositoryName(repositoryName);
    goNextStep();
  };

  const goBackToHome = () => {
    history.goBack();
  };

  const searchedRepositoryListItems = repositories
    ?.filter((repository) => repository.name.includes(searchText))
    .map((repository) => (
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

  const handleErrorConfirm = () => {
    history.push(PAGE_URL.HOME);
  };

  const handleSearchInputChange: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    setSearchText(event.currentTarget.value);
  };

  if (error) {
    error.response && showAlertModal(getAPIErrorMessage(error.response?.data.errorCode));

    return <MessageModalPortal heading={modalMessage} onConfirm={handleErrorConfirm} onClose={hideMessageModal} />;
  }

  if (isLoading) {
    return <PageLoading />;
  }

  return (
    <Container>
      <SearchInputWrapper>
        <Input kind="borderBottom" icon={<SearchIcon />} onChange={handleSearchInputChange} />
      </SearchInputWrapper>
      <RepositoryList>{searchedRepositoryListItems}</RepositoryList>
      {isModalShown && <MessageModalPortal heading={modalMessage} onConfirm={goBackToHome} onClose={goBackToHome} />}
    </Container>
  );
};

export default RepositorySelector;
