import styled from "styled-components";
import { LAYOUT, Z_INDEX } from "../../constants/layout";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery, setMobileMediaQuery } from "./mediaQueries";

export const Header = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: ${LAYOUT.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
  z-index: ${Z_INDEX.LOWER};
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
    padding: ${LAYOUT.PAGE_MARGIN_TOP} 3rem 0 3rem;
  `}

  ${setLaptopMediaQuery`
    padding: ${LAYOUT.PAGE_MARGIN_TOP} 12rem 0 12rem;
  `}

  ${setDesktopMediaQuery`
    padding: ${LAYOUT.PAGE_MARGIN_TOP} 24rem 0 24rem;
  `}
`;
