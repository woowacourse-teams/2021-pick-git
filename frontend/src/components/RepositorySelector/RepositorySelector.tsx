import { Dispatch, SetStateAction, useContext, useEffect, useRef, useState } from "react";
import { useHistory } from "react-router-dom";
import { ThemeContext } from "styled-components";
import { RepositoryIcon, SearchIcon } from "../../assets/icons";
import { FAILURE_MESSAGE, REDIRECT_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import useDebounce from "../../hooks/common/useDebounce";
import useMessageModal from "../../hooks/common/useMessageModal";
import { useGithubRepositoriesQuery } from "../../services/queries";
import { getAPIErrorMessage } from "../../utils/error";
import { getRepositoriesFromPages } from "../../utils/infiniteData";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";
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
  const {
    data: infiniteRepositoriesData,
    isLoading,
    error,
    isFetching,
    fetchNextPage,
  } = useGithubRepositoriesQuery(searchKeyword);
  const { modalMessage, isModalShown, showAlertModal, hideMessageModal } = useMessageModal();

  const { color } = useContext(ThemeContext);
  const history = useHistory();

  const changeSearchKeyword = useDebounce(() => {
    setSearchKeyword(temporarySearchKeyword);
  }, 150);

  useEffect(() => {
    changeSearchKeyword();
  }, [temporarySearchKeyword]);

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

  if (error) {
    error.response && showAlertModal(getAPIErrorMessage(error.response?.data.errorCode));

    return <MessageModalPortal heading={modalMessage} onConfirm={handleErrorConfirm} onClose={hideMessageModal} />;
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

  if (!infiniteRepositoriesData) {
    showAlertModal(FAILURE_MESSAGE.POST_REPOSITORY_NOT_LOADABLE);

    return <MessageModalPortal heading={modalMessage} onConfirm={handleErrorConfirm} onClose={hideMessageModal} />;
  }

  const repositories = getRepositoriesFromPages(infiniteRepositoriesData.pages);

  const searchedRepositoryListItems = isLoading ? (
    <PageLoading />
  ) : (
    repositories.map((repository) => (
      <RepositoryListItem key={repository.name} onClick={() => handleRepositorySelect(repository.name)}>
        <RepositoryCircle>
          <CircleIcon diameter="1.875rem" backgroundColor={color.tertiaryColor}>
            <RepositoryIcon />
          </CircleIcon>
        </RepositoryCircle>
        <RepositoryName>{repository.name}</RepositoryName>
      </RepositoryListItem>
    ))
  );

  if (repositories?.length === 0 && searchKeyword === "") {
    showAlertModal(REDIRECT_MESSAGE.NO_REPOSITORY_EXIST);
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

      {isModalShown && <MessageModalPortal heading={modalMessage} onConfirm={goBackToHome} onClose={goBackToHome} />}
    </Container>
  );
};

export default RepositorySelector;
