import { useContext, useState } from "react";
import { Link } from "react-router-dom";

import Button from "../../components/@shared/Button/Button";
import CircleIcon from "../../components/@shared/CircleIcon/CircleIcon";
import { GithubLargeIcon } from "../../assets/icons";
import { requestGetGithubAuthLink } from "../../services/requests";
import { PAGE_URL } from "../../constants/urls";
import { ButtonLoader, ButtonSpinnerWrapper, Container, Heading, HomeLinkText, Inner } from "./LoginPage.style";
import SnackBarContext from "../../contexts/SnackbarContext";
import Loader from "../../components/@shared/Loader/Loader";

const LoginPage = () => {
  const [isRequesting, setIsRequesting] = useState(false);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  const onRequestGithubLogin = async () => {
    try {
      setIsRequesting(true);
      const githubLoginUrl = await requestGetGithubAuthLink();

      window.location.replace(githubLoginUrl);
    } catch (error) {
      console.error(error);

      pushSnackbarMessage("요청하신 작업을 수행할 수 없습니다.");
    }
  };

  const LoginButton = () =>
    isRequesting ? (
      <ButtonLoader type="button" kind="roundedBlock" padding="0.875rem">
        깃허브 로그인
        <ButtonSpinnerWrapper>
          <Loader kind="spinner" size="1rem" />
        </ButtonSpinnerWrapper>
      </ButtonLoader>
    ) : (
      <Button type="button" kind="roundedBlock" onClick={onRequestGithubLogin} padding="0.875rem">
        깃허브 로그인
      </Button>
    );

  return (
    <Container>
      <Inner>
        <Heading>깃 - 들다</Heading>
        <CircleIcon diameter="10.5rem">
          <GithubLargeIcon />
        </CircleIcon>
        <LoginButton />
        <Link to={PAGE_URL.HOME}>
          <HomeLinkText>처음으로</HomeLinkText>
        </Link>
      </Inner>
    </Container>
  );
};

export default LoginPage;
