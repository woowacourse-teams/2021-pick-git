import { Container, ErrorImage, ErrorText } from "./PageError.style";
import CannotFindImage from "../../../assets/images/cannot-find.png";

export interface Props {
  errorMessage?: string;
}

const PageError = ({ errorMessage }: Props) => {
  return (
    <Container>
      <ErrorImage src={CannotFindImage} alt="에러 이미지" />
      <ErrorText>{errorMessage}</ErrorText>
    </Container>
  );
};

export default PageError;
