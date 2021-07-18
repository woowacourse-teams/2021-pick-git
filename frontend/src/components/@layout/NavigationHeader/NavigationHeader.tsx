import { Container, Navigation, HomeLink, NavigationItem, FlexWrapper } from "./NavigationHeader.style";
import { AddBoxIcon, HomeIcon, LoginIcon, PersonIcon, SearchIcon } from "../../../assets/icons";
import { PAGE_URL } from "../../../constants/urls";
import { useContext } from "react";
import UserContext from "../../../contexts/UserContext";
import Button from "../../@shared/Button/Button";

const NavigationHeader = () => {
  const { isLoggedIn, logout } = useContext(UserContext);

  const handleLogoutButtonClick = () => logout();

  const UnAuthenticatedNavigation = () => (
    <Navigation>
      <NavigationItem to={PAGE_URL.SEARCH}>
        <SearchIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_URL.LOGIN}>
        <LoginIcon />
      </NavigationItem>
    </Navigation>
  );

  const AuthenticatedNavigation = () => (
    <Navigation>
      <NavigationItem to={PAGE_URL.HOME}>
        <HomeIcon />
      </NavigationItem>
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
      <HomeLink to={PAGE_URL.HOME}>깃들다</HomeLink>
      {isLoggedIn ? (
        <FlexWrapper>
          <AuthenticatedNavigation />
          <Button kind="roundedInline" onClick={handleLogoutButtonClick}>
            Logout
          </Button>
        </FlexWrapper>
      ) : (
        <UnAuthenticatedNavigation />
      )}
    </Container>
  );
};

export default NavigationHeader;
