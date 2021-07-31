import { useContext, useEffect } from "react";
import { BrowserRouter, Redirect, Route, Switch } from "react-router-dom";

import { PAGE_URL } from "./constants/urls";
import LoginPage from "./pages/LoginPage/LoginPage";
import NavigationHeader from "./components/@layout/NavigationHeader/NavigationHeader";
import HomeFeedPage from "./pages/HomeFeedPage/HomeFeedPage";
import ProfilePage from "./pages/ProfilePage/ProfilePage";
import AuthLoginProcessingPage from "./pages/AuthLoginProcessingPage/AuthLoginProcessingPage";
import PostAddStepHeader from "./components/PostAddStepHeader/PostAddStepHeader";
import AddPostPage from "./pages/AddPostPage/AddPostPage";
import { PostAddDataContextProvider } from "./contexts/PostAddDataContext";
import UserFeedPage from "./pages/UserFeedPage/UserFeedPage";
import TagFeedPage from "./pages/TagFeedPage/TagFeedPage";
import SearchPage from "./pages/SearchPage/SearchPage";
import SearchHeader from "./components/@layout/SearchHeader/SearchHeader";
import UserContext from "./contexts/UserContext";
import { getAccessToken } from "./storage/storage";
import { requestGetSelfProfile } from "./services/requests";
import SnackBarContext from "./contexts/SnackbarContext";
import { SUCCESS_MESSAGE } from "./constants/messages";

const App = () => {
  const { currentUsername, login, logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  useEffect(() => {
    const accessToken = getAccessToken();

    if (!accessToken || !currentUsername) return;

    // haha
    // haha 2
    // haha 3
    // haha 4

    (async () => {
      try {
        const { name } = await requestGetSelfProfile(accessToken);

        login(accessToken, name);
        pushSnackbarMessage(SUCCESS_MESSAGE.LOGIN);
      } catch (error) {
        logout();
        pushSnackbarMessage(SUCCESS_MESSAGE.LOGOUT);
      }
    })();
  }, []);

  return (
    <BrowserRouter>
      <Switch>
        <Route exact path={[PAGE_URL.HOME, PAGE_URL.PROFILE, PAGE_URL.MY_PROFILE, PAGE_URL.USER_FEED]}>
          <NavigationHeader />
        </Route>
        <Route path={PAGE_URL.ADD_POST}>
          <PostAddStepHeader />
        </Route>
        <Route path={PAGE_URL.SEARCH}>
          <SearchHeader />
        </Route>
      </Switch>
      <Switch>
        <Route exact path={[PAGE_URL.HOME, PAGE_URL.HOME_FEED]}>
          <HomeFeedPage />
        </Route>
        <Route exact path={PAGE_URL.USER_FEED}>
          <UserFeedPage />
        </Route>
        <Route exact path={PAGE_URL.TAG_FEED_BASE}>
          <TagFeedPage />
        </Route>
        <Route exact path={PAGE_URL.SEARCH}>
          <SearchPage />
        </Route>
        <Route exact path={PAGE_URL.LOGIN}>
          <LoginPage />
        </Route>
        <Route exact path={PAGE_URL.AUTH_PROCESSING}>
          <AuthLoginProcessingPage />
        </Route>
        <Route path={PAGE_URL.MY_PROFILE}>
          <ProfilePage isMyProfile={true} />
        </Route>
        <Route path={PAGE_URL.PROFILE}>
          <ProfilePage isMyProfile={false} />
        </Route>
        <Route path={PAGE_URL.ADD_POST}>
          <PostAddDataContextProvider>
            <AddPostPage />
          </PostAddDataContextProvider>
        </Route>
        <Redirect to="/" />
      </Switch>
    </BrowserRouter>
  );
};
export default App;
