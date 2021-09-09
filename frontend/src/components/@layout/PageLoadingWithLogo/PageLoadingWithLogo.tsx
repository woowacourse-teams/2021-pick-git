import { LogoLargeIcon as LogoIcon } from "../../../assets/icons";
import { Container, LogoIconWrapper } from "./PageLoadingWithLogo.style";

const PageLoadingWithLogo = () => {
  return (
    <Container>
      <LogoIconWrapper>
        <LogoIcon />
      </LogoIconWrapper>
    </Container>
  );
};

export default PageLoadingWithLogo;
