import { Link } from "react-router-dom";
import styled, { css } from "styled-components";
import { PAGE_WIDTH } from "../../../constants/layout";
import { Header } from "../../@styled/layout";
import {
  setLaptopMediaQuery,
  setTabletMediaQuery,
  setDesktopMediaQuery,
  setMobileMediaQuery,
} from "../../@styled/mediaQueries";

export const Container = styled(Header)<React.CSSProperties>``;

export const HeaderContent = styled.div`
  display: flex;
  align-items: center;
  padding: 1.0625rem 1.375rem;

  ${setTabletMediaQuery`
    width: ${PAGE_WIDTH.TABLET};
    margin: 0 auto;  
    padding: 1.2rem 1.375rem;
  `}

  ${setLaptopMediaQuery`
    width: ${PAGE_WIDTH.LAPTOP};
    margin: 0 auto;
    padding: 1.2rem 1.375rem;
  `}

  ${setDesktopMediaQuery`
    width: ${PAGE_WIDTH.DESKTOP};
    margin: 0 auto;  
    padding: 1.2rem 1.375rem;
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

export const Navigation = styled.nav<{ isLoggedIn?: boolean }>(
  ({ isLoggedIn }) => css`
    display: flex;
    align-items: center;
  `
);

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
  display: flex;
  align-items: center;
  margin-left: auto;

  ${setMobileMediaQuery`
    margin-left: 1.1875rem;
  `}

  ${setTabletMediaQuery`
    margin-left: 1rem;
  `}
`;
