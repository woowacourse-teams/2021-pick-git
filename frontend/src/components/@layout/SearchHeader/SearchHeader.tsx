import { useContext, useState } from "react";
import { useHistory } from "react-router-dom";

import Input from "../../@shared/Input/Input";
import { Container, GoBackLink, SearchInputWrapper } from "./SearchHeader.style";
import { GoBackIcon, SearchIcon } from "../../../assets/icons";
import SearchContext from "../../../contexts/SearchContext";
import useDebounce from "../../../services/hooks/@common/useDebounce";

const SearchHeader = () => {
  const [localKeyword, setLocalKeyword] = useState("");
  const history = useHistory();
  const { onKeywordChange } = useContext(SearchContext);

  const handleGoBack = () => {
    history.goBack();
  };

  const applyKeywordToContext = useDebounce(() => onKeywordChange(localKeyword), 300);

  const handleKeywordChange: React.ChangeEventHandler<HTMLInputElement> = ({ target: { value } }) => {
    setLocalKeyword(value);
    applyKeywordToContext();
  };

  return (
    <Container>
      <GoBackLink onClick={handleGoBack}>
        <GoBackIcon color="#5a5a5a" />
      </GoBackLink>
      <SearchInputWrapper>
        <Input
          kind="rounded"
          value={localKeyword}
          onChange={handleKeywordChange}
          icon={<SearchIcon />}
          placeholder="검색하기"
        />
      </SearchInputWrapper>
    </Container>
  );
};

export default SearchHeader;
