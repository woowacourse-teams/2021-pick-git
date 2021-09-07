import { useContext } from "react";
import { ThemeContext } from "styled-components";
import Loader from "../../@shared/Loader/Loader";
import { Container } from "./PageLoadingWithCover.style";

export interface Props {
  description: string;
}

const PageLoadingWithCover = ({ description }: Props) => {
  const theme = useContext(ThemeContext);

  return (
    <Container>
      {description}
      <Loader kind="dots" size="1rem" loaderColor={theme.color.borderColor} />
    </Container>
  );
};

export default PageLoadingWithCover;
