import { css } from "styled-components";
import { setMobileMediaQuery, setTabletMediaQuery } from "./mediaQueries";

export const customScrollbarCSS = css`
  color: transparent;
  text-shadow: 0 0 ${({ theme }) => theme.color.textColor};
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);
  transition: color 0.8s;

  :hover {
    color: ${({ theme }) => theme.color.tagItemColor};
  }

  ::-webkit-scrollbar {
    width: 20px;
  }

  ::-webkit-scrollbar-thumb {
    border-radius: 12px;
    border: 6px solid transparent;
    box-shadow: inset 0 0 0 10px;
  }

  ::-webkit-scrollbar-track {
    display: none;
  }

  ${setMobileMediaQuery`
    ::-webkit-scrollbar {
      display: none;
    }    
  `}

  ${setTabletMediaQuery`
    ::-webkit-scrollbar {
      display: none;
    }    
  `}
`;
