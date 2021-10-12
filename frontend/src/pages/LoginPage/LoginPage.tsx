import { useState } from "react";
import { Link } from "react-router-dom";

import Button from "../../components/@shared/Button/Button";
import CircleIcon from "../../components/@shared/CircleIcon/CircleIcon";
import Loader from "../../components/@shared/Loader/Loader";
import SVGIcon from "../../components/@shared/SVGIcon/SVGIcon";

import { PAGE_URL } from "../../constants/urls";

import useSnackbar from "../../hooks/common/useSnackbar";

import { requestGetGithubAuthLink } from "../../services/requests";

import { ButtonLoader, ButtonSpinnerWrapper, Container, Heading, HomeLinkText, Inner } from "./LoginPage.style";

const LoginPage = () => {
  const [isRequesting, setIsRequesting] = useState(false);
  const { pushSnackbarMessage } = useSnackbar();

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
      <ButtonLoader type="button" kind="roundedBlock">
        <ButtonSpinnerWrapper>
          <Loader kind="dots" size="1rem" />
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
          <SVGIcon icon="GithubLargeIcon" />
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
