import {
  Container,
  Navigation,
  HomeLink,
  NavigationItem,
  AuthNavigationWrapper,
  LogoIconWrapper,
  HeaderContent,
} from "./NavigationHeader.style";
import { AddBoxIcon, LoginIcon, LogoIcon, PersonIcon, SearchIcon } from "../../../assets/icons";
import { PAGE_URL } from "../../../constants/urls";
import { useContext } from "react";
import UserContext from "../../../contexts/UserContext";
import Button from "../../@shared/Button/Button";
import HomeFeedContext from "../../../contexts/HomeFeedContext";

const NavigationHeader = () => {
  const { isLoggedIn, logout } = useContext(UserContext);
  const { initHomeFeed, setCurrentPostId } = useContext(HomeFeedContext);

  const handleLogoutButtonClick = () => logout();

  const AuthenticatedNavigation = () => (
    <Navigation isLoggedIn={true}>
      <NavigationItem to={PAGE_URL.MY_PROFILE}>
        <PersonIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_URL.ADD_POST_FIRST_STEP}>
        <AddBoxIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_URL.SEARCH}>
        <SearchIcon />
      </NavigationItem>
    </Navigation>
  );

  return (
    <Container>
      <HeaderContent>
        <HomeLink
          to={PAGE_URL.HOME}
          onClick={() => {
            initHomeFeed();
            setCurrentPostId(-1);
          }}
        >
          <LogoIconWrapper>
            <LogoIcon />
          </LogoIconWrapper>
          깃들다
        </HomeLink>
        {isLoggedIn ? (
          <>
            <AuthenticatedNavigation />
            <AuthNavigationWrapper>
              <Button kind="roundedInline" padding="0.2rem 0.7rem 0.3rem" onClick={handleLogoutButtonClick}>
                로그아웃
              </Button>
            </AuthNavigationWrapper>
          </>
        ) : (
          <>
            <AuthNavigationWrapper>
              <NavigationItem to={PAGE_URL.SEARCH}>
                <SearchIcon />
              </NavigationItem>
              <NavigationItem to={PAGE_URL.LOGIN}>
                <LoginIcon />
              </NavigationItem>
            </AuthNavigationWrapper>
          </>
        )}
      </HeaderContent>
    </Container>
  );
};

export default NavigationHeader;
