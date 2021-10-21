import { useContext, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import Input from "../../@shared/Input/Input";
import { Container, HeaderContent, GoBackLink, SearchInputWrapper } from "./SearchHeader.style";
import { GoBackIcon, SearchIcon } from "../../../assets/icons";
import SearchContext from "../../../contexts/SearchContext";
import useDebounce from "../../../hooks/common/useDebounce";

const SearchHeader = () => {
  const defaultKeyword = new URLSearchParams(location.search).get("keyword");
  const [localKeyword, setLocalKeyword] = useState(defaultKeyword ?? "");
  const history = useHistory();
  const { keyword, onKeywordChange } = useContext(SearchContext);

  const handleGoBack = () => {
    history.goBack();
  };

  const applyKeywordToContext = useDebounce<string>((value) => onKeywordChange(value ?? ""), 300);

  const handleKeywordChange: React.ChangeEventHandler<HTMLInputElement> = ({ target: { value } }) => {
    setLocalKeyword(value);
  };

  useEffect(() => {
    onKeywordChange("");
  }, []);

  useEffect(() => {
    applyKeywordToContext(localKeyword);
  }, [localKeyword]);

  useEffect(() => {
    setLocalKeyword(keyword);
  }, [keyword]);

  return (
    <Container>
      <HeaderContent>
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
      </HeaderContent>
    </Container>
  );
};

export default SearchHeader;
