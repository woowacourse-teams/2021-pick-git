import styled, { css } from "styled-components";
import { LAYOUT, PAGE_WIDTH, Z_INDEX } from "../../constants/layout";
import { fadeIn } from "./keyframes";
import { setDesktopMediaQuery, setLaptopMediaQuery, setTabletMediaQuery, setMobileMediaQuery } from "./mediaQueries";
import { customScrollbarCSS } from "./scrollbar";

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

export const Page = styled.main(
  () => css`
    width: 100%;
    min-height: 100%;
    padding-top: ${LAYOUT.HEADER_HEIGHT};
    overflow-x: hidden;

    animation: ${fadeIn} 1s forwards;

    ${setTabletMediaQuery`
      padding: ${LAYOUT.PAGE_MARGIN_TOP} 0.3125rem 0;
      width: ${PAGE_WIDTH.TABLET};
      margin: 0 auto;
    `}

    ${setLaptopMediaQuery`
      padding: ${LAYOUT.PAGE_MARGIN_TOP} 0.3125rem 0;
      width: ${PAGE_WIDTH.LAPTOP};
      margin: 0 auto;
    `}

    ${setDesktopMediaQuery`
      padding: ${LAYOUT.PAGE_MARGIN_TOP} 0.3125rem 0;
      width: ${PAGE_WIDTH.DESKTOP};
      margin: 0 auto;
    `}
  `
);

export const ScrollPageWrapper = styled.div(
  ({ theme }) => css`
    width: 100%;
    height: 100%;
    overflow-y: scroll;

    ${customScrollbarCSS(theme.color.tagItemColor)}
  `
);

export const NoneStyledTextarea = styled.textarea`
  background: none;
  border: none;
  margin: 0;

  :focus {
    outline: none;
  }
`;
