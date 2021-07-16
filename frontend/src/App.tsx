import { BrowserRouter, Redirect, Route, Switch } from "react-router-dom";
import { useContext, useEffect } from "react";

import { PAGE_URL } from "./constants/urls";
import LoginPage from "./pages/LoginPage/LoginPage";
import NavigationHeader from "./components/@layout/NavigationHeader/NavigationHeader";
import HomeFeedPage from "./pages/HomeFeedPage/HomeFeedPage";
import ProfilePage from "./pages/ProfilePage/ProfilePage";
import { Page } from "./components/@styled/layout";
import UserContext from "./contexts/UserContext";
import AuthLoginProcessingPage from "./pages/AuthLoginProcessingPage/AuthLoginProcessingPage";

const App = () => {
  const { login } = useContext(UserContext);

  useEffect(() => {
    login("testToken", "tanney-102");
  }, []);

  return (
    <BrowserRouter>
      <Switch>
        <Route exact path={[PAGE_URL.HOME, PAGE_URL.PROFILE]}>
          <NavigationHeader isLoggedIn={false} />
        </Route>
      </Switch>
      <Page>
        <Switch>
          <Route exact path={PAGE_URL.HOME}>
            <HomeFeedPage />
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
          <Redirect to="/" />
        </Switch>
      </Page>
    </BrowserRouter>
  );
};
export default App;
