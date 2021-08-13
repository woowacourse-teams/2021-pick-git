import styled from "styled-components";
import { LAYOUT, PAGE_WIDTH, Z_INDEX } from "../../constants/layout";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery, setMobileMediaQuery } from "./mediaQueries";

export const Header = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: ${LAYOUT.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
  z-index: ${Z_INDEX.LOW};
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2);

  ${setMobileMediaQuery`
    box-shadow: none;
  `}
`;

export const Page = styled.main`
  width: 100%;
  height: 100%;
  padding-top: ${LAYOUT.HEADER_HEIGHT};

  ${setTabletMediaQuery`
    padding-top: ${LAYOUT.PAGE_MARGIN_TOP};
    width: ${PAGE_WIDTH.TABLET};
    margin: 0 auto;
  `}

  ${setLaptopMediaQuery`
    padding-top: ${LAYOUT.PAGE_MARGIN_TOP};
    width: ${PAGE_WIDTH.LAPTOP};
    margin: 0 auto;
  `}

  ${setDesktopMediaQuery`
    padding-top: ${LAYOUT.PAGE_MARGIN_TOP};
    width: ${PAGE_WIDTH.DESKTOP};
    margin: 0 auto;
  `}
`;
