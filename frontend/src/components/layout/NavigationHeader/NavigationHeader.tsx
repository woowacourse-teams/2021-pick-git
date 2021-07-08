import { Container, HomeLink, Navigation, NavigationItem } from "./NavigationHeader.style";
import { AddBoxIcon, HomeIcon, LoginIcon, PersonIcon, SearchIcon } from "../../../assets/icons";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  isLoggedIn: boolean;
}

const NavigationHeader = ({ isLoggedIn }: Props) => {
  const unAuthenticatedNavigation = (
    <Navigation>
      <NavigationItem>
        <SearchIcon />
      </NavigationItem>
      <NavigationItem>
        <LoginIcon />
      </NavigationItem>
    </Navigation>
  );

  const authenticatedNavigation = (
    <Navigation>
      <NavigationItem>
        <HomeIcon />
      </NavigationItem>
      <NavigationItem>
        <PersonIcon />
      </NavigationItem>
      <NavigationItem>
        <AddBoxIcon />
      </NavigationItem>
      <NavigationItem>
        <SearchIcon />
      </NavigationItem>
    </Navigation>
  );

  return (
    <Container>
      <HomeLink>깃들다</HomeLink>
      {isLoggedIn ? authenticatedNavigation : unAuthenticatedNavigation}
    </Container>
  );
};

export default NavigationHeader;
