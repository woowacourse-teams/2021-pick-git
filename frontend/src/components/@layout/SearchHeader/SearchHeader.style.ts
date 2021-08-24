import styled from "styled-components";
import { PAGE_WIDTH } from "../../../constants/layout";
import { Header } from "../../@styled/layout";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled(Header)<React.CSSProperties>``;

export const HeaderContent = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.4rem 1.4375rem;

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
