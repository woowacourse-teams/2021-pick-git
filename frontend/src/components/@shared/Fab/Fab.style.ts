import styled, { css, CSSProp } from "styled-components";
import { Z_INDEX } from "../../../constants/layout";
import { setLaptopAboveMediaQuery } from "../../@styled/mediaQueries";

export const BackDrop = styled.div<{ isShown: boolean }>(
  ({ isShown }) => css`
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: ${Z_INDEX.MIDDLE};
    background-color: rgba(256, 256, 256, 0.85);
    transition: opacity 1s;

    ${isShown
      ? `
      opacity: 1; 
      pointer-events: initial
      `
      : `
      opacity: 0; 
      pointer-events: none;
      `}
  `
);

export const StyledFab = styled.button<{ color?: string; backgroundColor?: string; cssProp?: CSSProp }>(
  ({ theme, color, backgroundColor }) => css`
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    width: 3rem;
    height: 3rem;
    right: 1rem;
    bottom: 1rem;
    z-index: ${Z_INDEX.MIDDLE};
    border-radius: 50%;

    color: ${color ?? theme.color.white};
    background-color: ${backgroundColor ?? theme.color.primaryColor};
    box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.2);
    transition: opacity 0.5s;

    :hover {
      opacity: 0.7;
    }

    ${setLaptopAboveMediaQuery`
      right: 2rem;
      bottom: 2rem;
    `}
  `
);

export const ChildFabWrapper = styled.div<{ index: number; isShown: boolean }>(
  ({ index, isShown }) => css`
    display: flex;
    align-items: center;
    position: absolute;
    right: 1rem;
    bottom: 1rem;
    z-index: ${Z_INDEX.MIDDLE};
    transition: transform 1s, opacity 1s;

    ${isShown
      ? `
      transform: translateY(-${(index + 1) * 5}rem);
      opacity: 1;
      pointer-events: initial;
    `
      : `
      transform: transform: translateY(0);
      opacity: 0;
      pointer-events: none;
    `}

    ${setLaptopAboveMediaQuery`
      right: 2rem;
      bottom: 2rem;
    `}
  `
);

export const FabText = styled.span<{ isShown: boolean }>(
  ({ theme, isShown }) => css`
    display: ${isShown ? "inline-block" : "none"};
    color: ${theme.color.tertiaryColor};
    font-size: 1rem;
    margin-right: 1rem;
    transition: opacity 1s ${isShown ? "1s" : "0s"};

    opacity: ${isShown ? "1" : "0"};

    ${setLaptopAboveMediaQuery`
      margin-right: 1.5rem;
      font-size: 1.2rem;
    `}
  `
);

export const ChildFab = styled.button<{ color?: string; backgroundColor?: string; isShown: boolean }>(
  ({ theme, color, backgroundColor, isShown }) => css`
    display: flex;
    justify-content: center;
    align-items: center;
    width: 3rem;
    height: 3rem;
    border-radius: 50%;
    color: ${color ?? theme.color.white};
    background-color: ${backgroundColor ?? theme.color.tagItemColor};
    transition: transform 1s, opacity 0.5s;

    ${isShown
      ? `
      transform: scale(1);
    `
      : `
      transform: scale(0);
    `}

    :hover {
      opacity: 0.7;
    }
  `
);
