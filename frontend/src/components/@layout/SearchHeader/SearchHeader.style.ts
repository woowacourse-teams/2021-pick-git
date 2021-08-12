import styled from "styled-components";
import { Header } from "../../@styled/layout";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled(Header)<React.CSSProperties>`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.4rem 1.4375rem;

  ${setTabletMediaQuery`
    padding: 0.4rem 3.5rem;
  `}

  ${setLaptopMediaQuery`
    padding: 0.4rem 12.5rem;
  `}

  ${setDesktopMediaQuery`
    padding: 0.4rem 24.5rem;
  `}
`;

export const GoBackLink = styled.a`
  transition: opacity 0.5s;
  margin-right: 0.9375rem;

  :hover {
    opacity: 0.5;
  }
`;

export const SearchInputWrapper = styled.div`
  flex-grow: 1;
  height: 100%;

  input {
    height: 100%;
  }
`;
