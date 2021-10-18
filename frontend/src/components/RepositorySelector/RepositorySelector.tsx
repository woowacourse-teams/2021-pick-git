import { Dispatch, SetStateAction, useContext, useEffect, useRef, useState } from "react";
import { useHistory } from "react-router-dom";
import { ThemeContext, useTheme } from "styled-components";
import { RepositoryIcon, SearchIcon } from "../../assets/icons";
import { FAILURE_MESSAGE, REDIRECT_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import useDebounce from "../../hooks/common/useDebounce";
import useModal from "../../hooks/common/useModal";
import { useGithubRepositoriesQuery } from "../../services/queries";
import { getAPIErrorMessage } from "../../utils/error";
import { getRepositoriesFromPages } from "../../utils/infiniteData";
import AlertPortal from "../@layout/AlertPortal/AlertPortal";
import PageLoading from "../@layout/PageLoading/PageLoading";
import CircleIcon from "../@shared/CircleIcon/CircleIcon";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import Input from "../@shared/Input/Input";
import {
  Container,
  SearchInputWrapper,
  RepositoryList,
  RepositoryListItem,
  RepositoryCircle,
  RepositoryName,
  SearchResultNotFound,
  GoBackLink,
} from "./RepositorySelector.style";

interface Props {
  setGithubRepositoryName: Dispatch<SetStateAction<string>>;
  goNextStep: () => void;
}

const RepositorySelector = ({ setGithubRepositoryName, goNextStep }: Props) => {
  const [temporarySearchKeyword, setTemporarySearchKeyword] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");
  const history = useHistory();

  const { color } = useTheme();

  const {
    data: infiniteRepositoriesData,
    isLoading,
    error,
    isFetching,
    fetchNextPage,
  } = useGithubRepositoriesQuery(searchKeyword);
  const {
    modalMessage: alertMessage,
    isModalShown: isAlertShown,
    showModal: showAlert,
    hideModal: hideAlert,
  } = useModal();

  const repositories = getRepositoriesFromPages(infiniteRepositoriesData?.pages);

  const searchedRepositoryListItems =
    repositories?.map((repository) => (
      <RepositoryListItem key={repository.name} onClick={() => handleRepositorySelect(repository.name)}>
        <RepositoryCircle>
          <CircleIcon diameter="1.875rem" backgroundColor={color.tertiaryColor}>
            <RepositoryIcon />
          </CircleIcon>
        </RepositoryCircle>
        <RepositoryName>{repository.name}</RepositoryName>
      </RepositoryListItem>
    )) ?? [];

  const changeSearchKeyword = useDebounce(() => {
    setSearchKeyword(temporarySearchKeyword);
  }, 150);

  const handleRepositorySelect = (repositoryName: string) => {
    setGithubRepositoryName(repositoryName);
    goNextStep();
  };

  const goBackToHome = () => {
    history.goBack();
  };

  const handleErrorConfirm = () => {
    history.push(PAGE_URL.HOME);
  };

  const handleSearchInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = event.currentTarget;

    setTemporarySearchKeyword(value);
  };

  useEffect(() => {
    changeSearchKeyword();
  }, [temporarySearchKeyword]);

  if (error) {
    error.response && showAlert(getAPIErrorMessage(error.response?.data.errorCode));

    return <AlertPortal heading={alertMessage} onOkay={handleErrorConfirm} />;
  }

  if (isLoading) {
    return (
      <Container>
        <SearchInputWrapper>
          <Input kind="borderBottom" icon={<SearchIcon />} onChange={handleSearchInputChange} />
        </SearchInputWrapper>
        <PageLoading />
      </Container>
    );
  }

  if (!repositories) {
    showAlert(FAILURE_MESSAGE.POST_REPOSITORY_NOT_LOADABLE);

    return <AlertPortal heading={alertMessage} onOkay={handleErrorConfirm} />;
  }

  if (repositories?.length === 0 && searchKeyword === "") {
    showAlert(REDIRECT_MESSAGE.NO_REPOSITORY_EXIST);
  }

  return (
    <Container>
      <SearchInputWrapper>
        <Input kind="borderBottom" icon={<SearchIcon />} onChange={handleSearchInputChange} />
      </SearchInputWrapper>
      {repositories?.length !== 0 ? (
        <RepositoryList>
          <InfiniteScrollContainer isLoaderShown={isFetching} onIntersect={fetchNextPage}>
            {searchedRepositoryListItems}
          </InfiniteScrollContainer>
        </RepositoryList>
      ) : (
        <SearchResultNotFound>
          검색 결과를 찾을 수 없습니다
          <GoBackLink to={PAGE_URL.HOME}>홈으로 돌아가기</GoBackLink>
        </SearchResultNotFound>
      )}

      {isAlertShown && <AlertPortal heading={alertMessage} onOkay={goBackToHome} />}
    </Container>
  );
};

export default RepositorySelector;
