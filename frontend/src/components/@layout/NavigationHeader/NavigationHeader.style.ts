import { Link } from "react-router-dom";
import styled from "styled-components";
import { Header } from "../../@styled/layout";
import {
  setLaptopMediaQuery,
  setTabletMediaQuery,
  setDesktopMediaQuery,
  setMobileMediaQuery,
} from "../../@styled/mediaQueries";

export const Container = styled(Header)<React.CSSProperties>`
  display: flex;
  align-items: center;
  padding: 1.0625rem 1.375rem;

  ${setTabletMediaQuery`
    padding: 1.2rem 3.5rem;
    box-shadow: 0px 2px 4px rgba(0,0,0,0.2);
  `}

  ${setLaptopMediaQuery`
    padding: 1.2rem 12.5rem;
    box-shadow: 0px 2px 4px rgba(0,0,0,0.2);
  `}

  ${setDesktopMediaQuery`
    padding: 1.2rem 24.5rem;
    box-shadow: 0px 2px 4px rgba(0,0,0,0.2);
  `}
`;

export const LogoIconWrapper = styled.div`
  position: relative;
  bottom: 0.2rem;
  margin-right: 0.5rem;
`;

export const HomeLink = styled(Link)`
  display: flex;
  align-items: center;
  font-family: "jua";
  height: fit-content;
  margin-right: auto;
  color: ${({ theme }) => theme.color.primaryColor};
`;

export const FlexWrapper = styled.div`
  display: flex;
  justify-self: flex-end;
  align-items: center;

  button {
    margin-left: 1.5rem;
  }
`;

export const Navigation = styled.nav`
  display: flex;
  align-items: center;

  ${setLaptopMediaQuery`
    justify-self: center;
  `}
`;

export const NavigationItem = styled(Link)`
  transition: opacity 0.5s;

  :not(:last-child) {
    margin-right: 1.1875rem;

    ${setLaptopMediaQuery`
      margin-right: 1.5rem;
    `}

    ${setDesktopMediaQuery`
      margin-right: 2.5rem;
    `}
  }

  :hover {
    opacity: 0.5;
  }
`;

export const AuthNavigationWrapper = styled.div`
  transition: opacity 0.5s;
  margin-left: auto;

  ${setMobileMediaQuery`
    margin-left: 1.1875rem;
  `}

  ${setTabletMediaQuery`
    margin-left: 1rem;
  `}


  :hover {
    opacity: 0.5;
  }
`;
