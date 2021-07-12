import Input from "../../@shared/Input/Input";
import { Container, GoBackLink, SearchInputWrapper } from "./SearchHeader.style";
import { GoBackIcon } from "../../../assets/icons";
import { useHistory } from "react-router-dom";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {}

const SearchHeader = ({}: Props) => {
  const history = useHistory();

  const handleGoBack = () => {
    history.goBack();
  };

  return (
    <Container>
      <GoBackLink onClick={handleGoBack}>
        <GoBackIcon color="#5a5a5a" />
      </GoBackLink>
      <SearchInputWrapper>
        <Input kind="rounded" />
      </SearchInputWrapper>
    </Container>
  );
};

export default SearchHeader;
