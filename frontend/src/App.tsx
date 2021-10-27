import { useContext, useEffect, lazy } from "react";
import { BrowserRouter, Route, Redirect, Switch } from "react-router-dom";

import { PAGE_URL } from "./constants/urls";
import { SUCCESS_MESSAGE } from "./constants/messages";
import { getAccessToken } from "./storage/storage";
import { requestGetSelfProfile } from "./services/requests";

import { PostAddDataContextProvider } from "./contexts/PostAddDataContext";
import UserContext from "./contexts/UserContext";
import SnackBarContext from "./contexts/SnackbarContext";
import { PostEditStepContextProvider } from "./contexts/PostEditStepContext";
import MyPortfolioPage from "./pages/PortfolioPage/MyPortfolioPage";

const NavigationHeader = lazy(() => import("./components/@layout/NavigationHeader/NavigationHeader"));
const OneDepthStepHeader = lazy(() => import("./components/OneDepthStepHeader/OneDepthStepHeader"));
const PostAddStepHeader = lazy(() => import("./components/PostAddStepHeader/PostAddStepHeader"));
const SearchHeader = lazy(() => import("./components/@layout/SearchHeader/SearchHeader"));

const HomeFeedPage = lazy(() => import("./pages/HomeFeedPage/HomeFeedPage"));
const UserFeedPage = lazy(() => import("./pages/UserFeedPage/UserFeedPage"));
const SearchPage = lazy(() => import("./pages/SearchPage/SearchPage"));
const SearchPostResultPage = lazy(() => import("./pages/SearchPostResultPage/SearchPostResultPage"));
const LoginPage = lazy(() => import("./pages/LoginPage/LoginPage"));
const AuthLoginProcessingPage = lazy(() => import("./pages/AuthLoginProcessingPage/AuthLoginProcessingPage"));
const ProfilePage = lazy(() => import("./pages/ProfilePage/ProfilePage"));
const FollowingListPage = lazy(() => import("./pages/FollowingListPage/FollowingListPage"));
const FollowerListPage = lazy(() => import("./pages/FollowerListPage/FollowerListPage"));
const AddPostPage = lazy(() => import("./pages/AddPostPage/AddPostPage"));
const EditPostPage = lazy(() => import("./pages/EditPostPage/EditPostPage"));
const CommentsPage = lazy(() => import("./pages/CommentsPage/CommentsPage"));
const PostLikePeoplePage = lazy(() => import("./pages/PostLikePeoplePage/PostLikePeoplePage"));
const PortfolioPage = lazy(() => import("./pages/PortfolioPage/PortfolioPage"));

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
        <Route exact path={PAGE_URL.SEARCH_RESULT_FEED_BASE}>
          <OneDepthStepHeader title="검색결과" />
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
        <Route exact path={PAGE_URL.SEARCH}>
          <SearchPage />
        </Route>
        <Route exact path={PAGE_URL.SEARCH_RESULT_FEED_BASE}>
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
        <Route exact path={PAGE_URL.USER_FEED_BASE}>
          <UserFeedPage />
        </Route>
        <Route path={PAGE_URL.FOLLOWINGS_BASE}>
          <FollowingListPage />
        </Route>
        <Route path={PAGE_URL.FOLLOWERS_BASE}>
          <FollowerListPage />
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
        <Route path={PAGE_URL.POST_DETAIL}>
          <CommentsPage />
        </Route>
        <Route path={PAGE_URL.POST_LIKE_PEOPLE}>
          <PostLikePeoplePage />
        </Route>
        <Route path={PAGE_URL.PORTFOLIO}>
          <PortfolioPage />
        </Route>
        <Route path={PAGE_URL.MY_PORTFOLIO}>
          <MyPortfolioPage />
        </Route>
        <Redirect to="/" />
      </Switch>
    </BrowserRouter>
  );
};
export default App;
