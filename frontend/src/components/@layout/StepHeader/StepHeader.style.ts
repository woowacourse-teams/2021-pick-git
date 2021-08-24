import styled from "styled-components";
import { PAGE_WIDTH } from "../../../constants/layout";
import { Header } from "../../@styled/layout";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled(Header)<React.CSSProperties>``;

export const HeaderContent = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 1.125rem 1.5rem;

  ${setTabletMediaQuery`
    width: ${PAGE_WIDTH.TABLET};
    margin: 0 auto;
  `}

  ${setLaptopMediaQuery`
    width: ${PAGE_WIDTH.LAPTOP};
    margin: 0 auto;
  `}

  ${setDesktopMediaQuery`
    width: ${PAGE_WIDTH.DESKTOP};
    margin: 0 auto;
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
