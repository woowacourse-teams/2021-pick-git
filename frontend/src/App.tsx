import { useContext, useEffect } from "react";
import { BrowserRouter, Route, Redirect, Switch } from "react-router-dom";

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
import EditPostPage from "./pages/EditPostPage/EditPostPage";
import { PostEditStepContextProvider } from "./contexts/PostEditStepContext";
import SearchPostResultPage from "./pages/SearchPostResultPage/SearchPostResultPage";
import FollowingList from "./pages/FollowingUserListPage/FollowingList";
import FollowerList from "./pages/FollowerList/FollowerList";
import OneDepthStepHeader from "./components/OneDepthStepHeader/OneDepthStepHeader";
import CommentsPage from "./pages/CommentsPage/CommentsPage";
import PostLikePeoplePage from "./pages/PostLikePeoplePage/PostLikePeoplePage";

const App = () => {
  const { currentUsername, login, logout } = useContext(UserContext);
  const { pushSnackbarMessage } = useContext(SnackBarContext);

  useEffect(() => {
    const accessToken = getAccessToken();

    if (!accessToken || !currentUsername) return;

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
        <Route exact path={[PAGE_URL.HOME, PAGE_URL.PROFILE, PAGE_URL.MY_PROFILE, PAGE_URL.USER_FEED_BASE]}>
          <NavigationHeader />
        </Route>
        <Route exact path={PAGE_URL.FOLLOWINGS_BASE}>
          <OneDepthStepHeader title="팔로잉 목록" />
        </Route>
        <Route exact path={PAGE_URL.FOLLOWERS_BASE}>
          <OneDepthStepHeader title="팔로우 목록" />
        </Route>
        <Route path={PAGE_URL.ADD_POST}>
          <PostAddStepHeader />
        </Route>
        <Route path={[PAGE_URL.SEARCH, PAGE_URL.POST_LIKE_PEOPLE]}>
          <SearchHeader />
        </Route>
      </Switch>
      <Switch>
        <Route exact path={[PAGE_URL.HOME, PAGE_URL.HOME_FEED]}>
          <HomeFeedPage />
        </Route>
        <Route exact path={PAGE_URL.USER_FEED_BASE}>
          <UserFeedPage />
        </Route>
        <Route exact path={PAGE_URL.TAG_FEED_BASE}>
          <TagFeedPage />
        </Route>
        <Route exact path={PAGE_URL.SEARCH}>
          <SearchPage />
        </Route>
        <Route exact path={PAGE_URL.SEARCH_RESULT_POST_BASE}>
          <SearchPostResultPage />
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
        <Route path={PAGE_URL.FOLLOWINGS_BASE}>
          <FollowingList />
        </Route>
        <Route path={PAGE_URL.FOLLOWERS_BASE}>
          <FollowerList />
        </Route>
        <Route path={PAGE_URL.ADD_POST}>
          <PostAddDataContextProvider>
            <AddPostPage />
          </PostAddDataContextProvider>
        </Route>
        <Route path={PAGE_URL.EDIT_POST}>
          <PostEditStepContextProvider>
            <EditPostPage />
          </PostEditStepContextProvider>
        </Route>
        <Route path={PAGE_URL.POST_COMMENTS}>
          <CommentsPage />
        </Route>
        <Route path={PAGE_URL.POST_LIKE_PEOPLE}>
          <PostLikePeoplePage />
        </Route>
        <Redirect to="/" />
      </Switch>
    </BrowserRouter>
  );
};
export default App;
