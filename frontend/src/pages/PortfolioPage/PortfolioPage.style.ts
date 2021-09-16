import styled, { css } from "styled-components";
import {
  setDesktopMediaQuery,
  setLaptopMediaQuery,
  setLaptopAboveMediaQuery,
  setTabletMediaQuery,
} from "../../components/@styled/mediaQueries";
import { customScrollbarCSS } from "../../components/@styled/scrollbar";

export const Container = styled.main`
  height: 100%;
  scroll-snap-type: y mandatory;
  overflow-x: hidden;
  overflow-y: scroll;
  ${customScrollbarCSS};
`;

export const FullPage = styled.section<{ isVerticalCenter?: boolean }>`
  position: relative;
  scroll-snap-align: start;
  display: flex;
  flex-direction: column;
  justify-content: ${({ isVerticalCenter }) => (isVerticalCenter ? "center" : "flex-start")};
  align-items: center;
  width: 100vw;
  height: 100vh;
  overflow-y: scroll;
  padding: 2rem 0;

  ::-webkit-scrollbar {
    display: none;
  }
`;

export const UserAvatarCSS = css`
  height: 6.5625rem;
  margin-top: 3.125rem;
`;

export const UserNameCSS = css`
  height: 1.2rem;
  text-align: center;

  ${setLaptopAboveMediaQuery`
    font-size: 1.5rem;
  `}
`;

export const ToggleButtonCSS = css`
  position: absolute;
  top: 4rem;
  right: 1.375rem;

  ${setTabletMediaQuery`
    top: 5rem;
    right: 2rem;  
  `}

  ${setLaptopAboveMediaQuery`
    top: 6rem;
    right: 10rem;
  `}
`;

export const AvatarWrapper = styled.div``;

export const DescriptionCSS = css`
  ${({ theme }) => `
    color: ${theme.color.textColor};
  `}

  width: 100%;
  text-align: left;
  font-size: 0.75rem;
  margin: 1.25rem 0;
  line-height: 1.2rem;
  padding: 0 3rem;

  ${setLaptopMediaQuery`
    margin: 2.5rem 0;
    text-align: center;
    font-size: 0.8rem;
    border: none;
    padding: 0 12rem;
  `}

  ${setDesktopMediaQuery`
    margin: 4rem 0;
    text-align: center;
    font-size: 0.9rem;
    border: none;
    padding: 0 24rem;
  `}
`;

export const ContactWrapper = styled.div`
  display: flex;
  flex-direction: column;
  padding: 0 3rem;
  width: 100%;

  ${setLaptopMediaQuery`
    padding: 0 12rem;
    flex-direction: row;
    width: initial;
  `}

  ${setDesktopMediaQuery`
    padding: 0 24rem;
    flex-direction: row;
    width: initial;
  `}
`;

export const DetailInfo = styled.div`
  display: flex;
  align-items: center;
  width: 100%;
  color: ${({ theme }) => theme.color.textColor};
  line-height: 2.5;
  font-size: 0.875rem;
  margin-top: 1.5rem;

  ${setLaptopMediaQuery`
    font-size: 0.92rem;
    padding: 0 1rem;
  `}

  ${setDesktopMediaQuery`
    font-size: 1rem;
    padding: 0 3rem;
  `} 
  
  svg {
    margin-right: 1rem;
  }
`;

export const PaginatorWrapper = styled.div`
  position: absolute;
  right: 1.125rem;
  bottom: 1.5625rem;
`;

export const CategoryAddIconWrapper = styled.div`
  width: 100%;
  padding-left: 2rem;

  ${setLaptopAboveMediaQuery`
    padding-left: 22rem;
  
  `}
`;

export const ContactIconCSS = css`
  margin-right: 0.75rem;
`;

export const SectionNameCSS = css`
  text-align: right;
  align-self: flex-end;
  width: 50%;
  min-height: 3rem;
  padding: 0 1rem;
  margin: 0;
  font-size: 1.5rem;
  border-bottom: 2px solid ${({ theme }) => theme.color.primaryColor};

  ${setLaptopMediaQuery`
    width: 40%;
    min-height: 3.5rem;
    padding: 0 3rem;
    font-size: 1.8rem;
  `}

  ${setDesktopMediaQuery`
    width: 30%;
    min-height: 4.5rem;
    padding: 0 4rem;
    font-size: 2.2rem;
  `}
`;

export const CloseButtonWrapper = styled.div`
  position: absolute;
  top: 1.5rem;
  left: 1rem;
  display: flex;
  justify-content: flex-end;

  > button:active {
    transform: scale(0.98);
  }

  ${setLaptopMediaQuery`
    left: 4rem;
  `}

  ${setDesktopMediaQuery`
    left: 6rem;
  `}
`;
