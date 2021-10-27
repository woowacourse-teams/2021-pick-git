import { useContext, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import { GithubLargeIcon } from "../../assets/icons";
import { LOGIN_ANIMATION } from "../../constants/animation";
import { PAGE_URL } from "../../constants/urls";
import HomeFeedContext from "../../contexts/HomeFeedContext";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import { requestGetAccessToken } from "../../services/requests";
import { Container, Dot, DotWrapper, Text } from "./AuthLoginProcessingPage.style";

const AuthLoginProcessingPage = () => {
  const authCode = new URLSearchParams(location.search).get("code");
  const { initHomeFeed, setCurrentPostId } = useContext(HomeFeedContext);

  const [dotCount, setDotCount] = useState(0);
  const { login } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const history = useHistory();

  useEffect(() => {
    const timer = setInterval(() => {
      setDotCount((prevDotCount) => (prevDotCount + 1) % (LOGIN_ANIMATION.MAX_DOT_COUNT + 1));
    }, LOGIN_ANIMATION.DOT_COUNTING_INTERVAL);

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
        pushSnackbarMessage("로그인에 성공했습니다.");
      } catch (error) {
        console.error(error);

        pushSnackbarMessage("로그인에 실패했습니다.");
      } finally {
        initHomeFeed();
        setCurrentPostId(-1);
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
