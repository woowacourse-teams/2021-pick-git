import { Container, Navigation, HomeLink, NavigationItem } from "./NavigationHeader.style";
import { AddBoxIcon, HomeIcon, LoginIcon, PersonIcon, SearchIcon } from "../../../assets/icons";
import { PAGE_PATH } from "../../../constants/path";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  isLoggedIn: boolean;
}

const NavigationHeader = ({ isLoggedIn }: Props) => {
  const unAuthenticatedNavigation = (
    <Navigation>
      <NavigationItem to={PAGE_PATH.SEARCH}>
        <SearchIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_PATH.LOGIN}>
        <LoginIcon />
      </NavigationItem>
    </Navigation>
  );

  const authenticatedNavigation = (
    <Navigation>
      <NavigationItem to={PAGE_PATH.HOME}>
        <HomeIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_PATH.PROFILE}>
        <PersonIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_PATH.ADD_POST}>
        <AddBoxIcon />
      </NavigationItem>
      <NavigationItem to={PAGE_PATH.SEARCH}>
        <SearchIcon />
      </NavigationItem>
    </Navigation>
  );

  return (
    <Container>
      <HomeLink to={PAGE_PATH.HOME}>깃들다</HomeLink>
      {isLoggedIn ? authenticatedNavigation : unAuthenticatedNavigation}
    </Container>
  );
};

export default NavigationHeader;
