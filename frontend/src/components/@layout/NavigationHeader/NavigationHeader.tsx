import { Container, Navigation, HomeLink, NavigationItem } from "./NavigationHeader.style";
import { AddBoxIcon, HomeIcon, LoginIcon, PersonIcon, SearchIcon } from "../../../assets/icons";
import { PAGE_URL } from "../../../constants/urls";
import { useContext } from "react";
import UserContext from "../../../contexts/UserContext";

const NavigationHeader = () => {
  const { isLoggedIn } = useContext(UserContext);

  const unAuthenticatedNavigation = (
    <Navigation>
      <NavigationItem to={PAGE_URL.SEARCH}>
        <SearchIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_URL.LOGIN}>
        <LoginIcon />
      </NavigationItem>
    </Navigation>
  );

  const authenticatedNavigation = (
    <Navigation>
      <NavigationItem to={PAGE_URL.HOME}>
        <HomeIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_URL.MY_PROFILE}>
        <PersonIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_URL.ADD_POST}>
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
      {isLoggedIn ? authenticatedNavigation : unAuthenticatedNavigation}
    </Container>
  );
};

export default NavigationHeader;
