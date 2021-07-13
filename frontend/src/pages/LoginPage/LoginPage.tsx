import Button from "../../components/@shared/Button/Button";
import CircleIcon from "../../components/@shared/CircleIcon/CircleIcon";
import { GithubLargeIcon } from "../../assets/icons";
import { Container, Heading, Inner } from "./LoginPage.style";
import { requestGetGithubAuthLink } from "../../services/requests";

const LoginPage = () => {
  const onRequestGithubLogin = async () => {
    try {
      const githubLoginUrl = await requestGetGithubAuthLink();

      window.location.replace(githubLoginUrl);
    } catch (error) {
      console.error(error);

      alert("요청하신 작업을 수행할 수 없습니다.");
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
      </Inner>
    </Container>
  );
};

export default LoginPage;
