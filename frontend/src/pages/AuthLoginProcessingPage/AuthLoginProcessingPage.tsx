import { useContext, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import { GithubLargeIcon } from "../../assets/icons";
import { PAGE_URL } from "../../constants/urls";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { requestGetAccessToken } from "../../services/requests";
import { Container, Dot, DotWrapper, Text } from "./AuthLoginProcessingPage.style";

const MAX_DOT_COUNT = 3;
const DOT_COUNTING_INTERVAL = 500;

const AuthLoginProcessingPage = () => {
  const authCode = new URLSearchParams(location.search).get("code");

  const [dotCount, setDotCount] = useState(0);
  const { login } = useContext(UserContext);
  const { pushMessage } = useContext(SnackBarContext);
  const history = useHistory();

  useEffect(() => {
    const timer = setInterval(() => {
      setDotCount((prevDotCount) => (prevDotCount + 1) % (MAX_DOT_COUNT + 1));
    }, DOT_COUNTING_INTERVAL);

    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    (async () => {
      try {
        if (!authCode) {
          throw Error("no auth code");
        }

        const { token, username } = await requestGetAccessToken(authCode);

        login(token, username);
        pushMessage("로그인에 성공했습니다.");
      } catch (error) {
        console.error(error);

        pushMessage("로그인에 실패했습니다.");
      } finally {
        history.push(PAGE_URL.HOME);
      }
    })();
  }, []);

  return (
    <Container>
      <GithubLargeIcon />
      <Text>Login</Text>
      <DotWrapper>
        {[...Array(dotCount)].map((_, index) => (
          <Dot key={index} />
        ))}
      </DotWrapper>
    </Container>
  );
};

export default AuthLoginProcessingPage;
