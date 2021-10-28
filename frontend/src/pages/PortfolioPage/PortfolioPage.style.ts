import styled, { css } from "styled-components";
import { fadeIn } from "../../components/@styled/keyframes";
import {
  setDesktopMediaQuery,
  setLaptopMediaQuery,
  setLaptopAboveMediaQuery,
  setTabletMediaQuery,
} from "../../components/@styled/mediaQueries";
import { customScrollbarCSS } from "../../components/@styled/scrollbar";

export const Container = styled.main(
  ({ theme }) => css`
    background-color: ${theme.color.white};
    scroll-snap-type: y mandatory;
    overflow-x: hidden;
    overflow-y: scroll;
    height: 100%;

    animation: ${fadeIn} 1s forwards;

    ${customScrollbarCSS(theme.color.tagItemColor)}
  `
);

export const FullPage = styled.section<{ isVerticalCenter?: boolean }>`
  position: relative;
  scroll-snap-align: start;
  display: flex;
  flex-direction: column;
  justify-content: ${({ isVerticalCenter }) => (isVerticalCenter ? "center" : "flex-start")};
  align-items: center;
  width: 100vw;
  min-height: 100vh;
  overflow-y: scroll;
  padding-top: 4.375rem;

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
  min-height: 1.5rem;

  ${setLaptopAboveMediaQuery`
    font-size: 1.5rem;
    min-height: 1.8rem;
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

export const AvatarWrapper = styled.div(
  ({ theme }) => css`
    background-color: ${theme.color.white};

    ${setTabletMediaQuery`
    margin-top: 3rem;
  `}

    ${setLaptopAboveMediaQuery`
    margin-top: 5rem;
  `}
  `
);

export const DescriptionCSS = css`
  ${({ theme }) => `
    color: ${theme.color.textColor};
    background-color: ${theme.color.white};
  `}

  width: 100%;
  min-height: 5rem;
  text-align: left;
  font-size: 0.75rem;
  margin: 1rem 0;
  line-height: 1.2rem;
  padding: 0 3rem;

  ${setLaptopMediaQuery`
    margin: 1.5rem 0;
    text-align: center;
    font-size: 0.8rem;
    border: none;
    width: 70%;
  `}

  ${setDesktopMediaQuery`
    margin: 2rem 0;
    text-align: center;
    font-size: 0.9rem;
    border: none;
    width: 50%;
  `}
`;

export const ContactWrapper = styled.div(
  ({ theme }) => css`
    display: flex;
    flex-direction: column;
    padding: 0 3rem;
    width: 100%;

    ${setLaptopMediaQuery`
    width: 70%;
  `}

    ${setDesktopMediaQuery`
    width: 50%;
  `}
  `
);

export const DetailInfo = styled.div`
  display: flex;
  align-items: center;
  width: 100%;
  color: ${({ theme }) => theme.color.textColor};
  line-height: 2.5;
  font-size: 0.875rem;
  margin-top: 0.7rem;

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
  position: fixed;
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
  width: 20px;
  height: 20px;
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
  top: 4.375rem;
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

export const PostSelectorModalCSS = css`
  width: 50rem;
  height: 70rem;
`;

export const FabCSS = css`
  * {
    stroke: white;
  }
`;