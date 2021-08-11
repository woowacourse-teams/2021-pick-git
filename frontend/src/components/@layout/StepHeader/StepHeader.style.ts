import styled from "styled-components";
import { Header } from "../../@styled/layout";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled(Header)<React.CSSProperties>`
  display: flex;
  justify-content: space-between;
  padding: 1.125rem 1.5rem;

  ${setTabletMediaQuery`
    padding: 1.125rem 3.5rem;
  `}

  ${setLaptopMediaQuery`
    padding: 1.125rem 12.5rem;
  `}

  ${setDesktopMediaQuery`
    padding: 1.125rem 24.5rem;
  `}
`;

export const Content = styled.div`
  font-size: 1.0625rem;
`;

export const StepLink = styled.a`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const EmptySpace = styled.div`
  display: inline-block;
  width: 1.375rem;
`;
