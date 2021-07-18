import Button from "../../components/@shared/Button/Button";
import CircleIcon from "../../components/@shared/CircleIcon/CircleIcon";
import { GithubLargeIcon } from "../../assets/icons";
import { requestGetGithubAuthLink } from "../../services/requests";
import { PAGE_URL } from "../../constants/urls";
import { Container, Heading, HomeLinkText, Inner } from "./LoginPage.style";
import { Link } from "react-router-dom";
import { useContext } from "react";
import SnackBarContext from "../../contexts/SnackbarContext";

const LoginPage = () => {
  const { pushMessage } = useContext(SnackBarContext);

  const onRequestGithubLogin = async () => {
    try {
      const githubLoginUrl = await requestGetGithubAuthLink();

      window.location.replace(githubLoginUrl);
    } catch (error) {
      console.error(error);

      pushMessage("요청하신 작업을 수행할 수 없습니다.");
    }
  };

  return (
    <Container>
      <Inner>
        <Heading>깃 - 들다</Heading>
        <CircleIcon diameter="10.5rem">
          <GithubLargeIcon />
        </CircleIcon>
        <Button type="button" kind="roundedBlock" onClick={onRequestGithubLogin}>
          깃허브 로그인
        </Button>
        <Link to={PAGE_URL.HOME}>
          <HomeLinkText>처음으로</HomeLinkText>
        </Link>
      </Inner>
    </Container>
  );
};

export default LoginPage;
